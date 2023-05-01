#include <trace.h>

#define IRING_BUF_LEN 16
char  *iringbuf[IRING_BUF_LEN];
char **iringbuf_head = NULL;
char **iringbuf_tail = iringbuf + (IRING_BUF_LEN - 1);
char **iringbuf_curr = NULL;

void itrace_record(char *logbuf) {
  if (iringbuf_head == NULL) {
    iringbuf_head = iringbuf;
  }
  else {
    if (iringbuf_head != iringbuf_tail) {
      iringbuf_head++;
    }
    else {
      iringbuf_head = iringbuf;
    }
  }

  if (*iringbuf_head == NULL) {
    *iringbuf_head = (char *)malloc(sizeof(char) * 256);
  }
  strcpy(*iringbuf_head, logbuf);
  iringbuf_curr = iringbuf_head;
}

void itrace_display() {
  iringbuf_head = iringbuf;
  while (*iringbuf_head != NULL && iringbuf_head != iringbuf_tail) {
    if (iringbuf_head == iringbuf_curr) {
      printf("[itrace] ----> %s\n", *iringbuf_head);
    }
    else {
      printf("[itrace]       %s\n", *iringbuf_head);
    }
    char *iringbuf_temp = *iringbuf_head;
    free(iringbuf_temp);
    iringbuf_head++;
  }
}

void mtrace_display(char *type,
                    char *dir,
                    word_t addr,
                    word_t data,
                    word_t len) {
  if (strcmp(type, "process") == 0) {
    printf("[mtrace] addr: " FMT_WORD " data: " FMT_WORD " %s\n", addr,
                                                                  data,
                                                                  dir);
  }
  else if (strcmp(type, "result") == 0) {
    for (word_t i = 0; i < len; i++) {
      addr = CONFIG_MBASE + i * 4;
      data = paddr_read(CONFIG_MBASE + i * 4, 8);
      printf("[mtrace] addr: " FMT_WORD " data: " FMT_WORD "\n", addr, data);
    }
  }
}

#define ARR_LEN 1024 * 1024

static int    inst_func_call_depth = -1;
static char  *inst_func_name_arr[ARR_LEN];
static char **inst_func_name_head = inst_func_name_arr;
static char  *func_name_arr[ARR_LEN];

static int ftrace_is_elf_64(FILE *fp) {
  char buf[16];
  int  nread = fread(buf, 1, 16, fp);
  fseek(fp, 0, SEEK_SET);
  // 如果读到的数据小于16位则不是ELF64格式文件
  if (nread < 16) {
    return 0;
  }
  // 如果前4个字符存在不同则不是ELF64格式文件
  if (strncmp(buf, ELFMAG, SELFMAG)) {
    return 0;
  }
  // 如果缓存数据与标准不等则不是ELF64格式文件
  if (buf[EI_CLASS] != ELFCLASS64) {
    return 0;
  }

  return 1;
}

static char *ftrace_get_func(Elf64_Addr addr) {
  Elf64_Addr offset = addr - CONFIG_MBASE;
  Assert(offset < ARR_LEN, "Out of bounds: %ld", offset);
  if (func_name_arr[offset] != NULL) {
    return func_name_arr[offset];
  }
  else {
    return "";
  }
}

void ftrace_init(const char *elf_file) {
  if (elf_file != NULL) {
    FILE *fp = fopen(elf_file, "r");
    Assert(fp, "Can not open '%s'", elf_file);
    Assert(ftrace_is_elf_64(fp), "File type mismatch");

    // 读取ELF文件头部
    Elf64_Ehdr elf_header;
    fread(&elf_header, 1, sizeof(elf_header), fp);

    // 读取ELF节区头部
    Elf64_Shdr elf_section_arr[elf_header.e_shnum];
    fseek(fp, elf_header.e_shoff, SEEK_SET);
    fread(elf_section_arr,
          sizeof(Elf64_Shdr),
          (elf_header.e_shnum * elf_header.e_shentsize),
          fp);

    // 读取ELF节区符号表和节区字符表
    Elf64_Shdr elf_section_symbol;
    Elf64_Shdr elf_section_string;
    for (int i = 0; i < elf_header.e_shnum; i++) {
      if (elf_section_arr[i].sh_type == SHT_SYMTAB) {
        memcpy(&elf_section_symbol, &elf_section_arr[i], sizeof(Elf64_Shdr));
        if (elf_section_arr[elf_section_symbol.sh_link].sh_type == SHT_STRTAB) {
          memcpy(&elf_section_string,
                 &elf_section_arr[elf_section_symbol.sh_link],
                 sizeof(Elf64_Shdr));
        }
      }
    }

    // 读取ELF字符表
    char elf_string_name_arr[elf_section_string.sh_size];
    fseek(fp, elf_section_string.sh_offset, SEEK_SET);
    fread(&elf_string_name_arr, 1, sizeof(elf_string_name_arr), fp);

    // 读取ELF符号表
    Elf64_Xword elf_symbol_cnt = elf_section_symbol.sh_size /
                                 elf_section_symbol.sh_entsize;
    Elf64_Sym elf_symbol_arr[elf_symbol_cnt];
    fseek(fp, elf_section_symbol.sh_offset, SEEK_SET);
    fread(&elf_symbol_arr, 1, elf_section_symbol.sh_size, fp);
    for (int i = 0; i < elf_symbol_cnt; i++) {
      uint8_t st_info = elf_symbol_arr[i].st_info;
      // 根据符号表名称偏移量读取字符表中的对应内容
      if (ELF64_ST_TYPE(st_info) == STT_FUNC) {
        Elf64_Addr st_value = elf_symbol_arr[i].st_value;
        Elf32_Word st_name = elf_symbol_arr[i].st_name;
        char *func_name = elf_string_name_arr + st_name;
        Elf64_Addr offset = st_value - elf_header.e_entry;
        Assert(offset < ARR_LEN, "Out of bounds: %ld", offset);
        if (func_name_arr[offset] == NULL) {
            func_name_arr[offset] = (char *)malloc(sizeof(char) * 256);
        }
        strcpy(func_name_arr[offset], func_name);
#ifdef CONFIG_ELF
        printf("Symbol Address: " FMT_WORD "\n", st_value);
        printf("Function Name:  %s\n\n", func_name);
#endif
      }
    }

    fclose(fp);
  }
}

char  *ftrace_info_arr[ARR_LEN];
char **ftrace_info_head = ftrace_info_arr;

void ftrace_display(char *type,
                    bool *inst_func_call,
                    bool *inst_func_ret,
                    word_t pc,
                    word_t dpnc) {
  if (strcmp(type, "result") == 0) {
    ftrace_info_head = ftrace_info_arr;
    while (*ftrace_info_head != NULL) {
      printf("%s", *ftrace_info_head);
      char *ftrace_info_temp = *ftrace_info_head;
      free(ftrace_info_temp);
      ftrace_info_head++;
    }
    return;
  }

  char buf[256];
  char buf_head[128];
  char buf_tail[128];

  if (*inst_func_call || *inst_func_ret) {
    sprintf(buf_head, "[ftrace] addr: 0x%08"PRIx32, (uint32_t)pc);
  }

  if (*inst_func_call) {
    inst_func_name_head++;
    inst_func_call_depth++;
    if (*inst_func_name_head == NULL) {
      *inst_func_name_head = (char *)malloc(sizeof(char *) * 256);
    }
    strcpy(*inst_func_name_head, ftrace_get_func(dpnc));
    char printf_format[] = " call %*s[%s@" "0x%08"PRIx32 "]\n";
    sprintf(buf_tail, printf_format, inst_func_call_depth * 2, "", *inst_func_name_head, (uint32_t)dpnc);
  }

  if (*inst_func_ret) {
    inst_func_name_head--;
    inst_func_call_depth--;
    char printf_format[] = " ret  %*s[%s]\n";
    sprintf(buf_tail, printf_format, inst_func_call_depth * 2, "", *inst_func_name_head);
  }

  if (*inst_func_call || *inst_func_ret) {
    sprintf(buf, "%s%s", buf_head, buf_tail);
    if (*ftrace_info_head == NULL) {
      *ftrace_info_head = (char *)malloc(sizeof(char *) * 256);
    }
    strcpy(*ftrace_info_head, buf);
    ftrace_info_head++;

    if (strcmp(type, "process") == 0) {
      printf("%s", buf);
    }
  }

  if (*inst_func_call) {
    *inst_func_call = false;
  }
  if (*inst_func_ret) {
    *inst_func_ret = false;
  }
}

void ftrace_free() {
  for (int i = 0; i < ARR_LEN; i++) {
    free(func_name_arr[i]);
  }
}
