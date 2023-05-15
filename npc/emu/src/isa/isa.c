#include <isa/isa.h>
#include <memory/memory.h>

void initISA() {
    const uint32_t img[] = {
        0x00100093, // addi r1 r0 1
        0x00A00193, // addi r3 r0 10
        0x00100073  // ebreak
    };
    memcpy(convertGuestToHost(RESET_VECTOR), img, sizeof(img));

    cpu.pc = RESET_VECTOR;
    cpu.gpr[0] = 0;
}
