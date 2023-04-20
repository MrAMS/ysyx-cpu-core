import chisel3._
import chisel3.util._

import cpu.comp._
import cpu.dpi._
import cpu.stage._
import cpu.util.Base._

class Top extends Module {
    val io = IO(new Bundle {
        val iInst =  Input(UInt(DATA_WIDTH.W))
        val oPC   = Output(UInt(DATA_WIDTH.W))
    });

    val ifu = Module(new IFU())
    io.oPC := ifu.io.oPC

    val mem = Module(new MemM())
    mem.io.iMemRdAddr := 0.U(DATA_WIDTH.W)

    val reg = Module(new RegM())

    val idu = Module(new IDU())
    idu.io.iInst       := io.iInst
    idu.io.iInstRS1Val := reg.io.oRegRd1Data
    idu.io.iInstRS2Val := reg.io.oRegRd2Data
    idu.io.iPC         := ifu.io.oPC

    reg.io.iRegRd1Addr := idu.io.oInstRS1Addr
    reg.io.iRegRd2Addr := idu.io.oInstRS2Addr

    val exu = Module(new EXU())
    exu.io.iInstRS1Addr := idu.io.oInstRS1Addr
    exu.io.iInstRS2Addr := idu.io.oInstRS2Addr
    exu.io.iInstRDAddr  := idu.io.oInstRDAddr
    exu.io.iInstRS1Val  := idu.io.oInstRS1Val
    exu.io.iInstRS2Val  := idu.io.oInstRS2Val
    exu.io.iPC          := ifu.io.oPC
    exu.io.iALUType     := idu.io.oALUType
    exu.io.iALURS1Val   := idu.io.oALURS1Val
    exu.io.iALURS2Val   := idu.io.oALURS2Val
    exu.io.iJmpEn       := idu.io.oJmpEn
    exu.io.iMemWrEn     := idu.io.oMemWrEn
    exu.io.iRegWrEn     := idu.io.oRegWrEn
    exu.io.iRegWrSrc    := idu.io.oRegWrSrc
    exu.io.iCsrWrEn     := idu.io.oCsrWrEn

    val amu = Module(new AMU())
    amu.io.iMemWrEn   := exu.io.oMemWrEn
    amu.io.iMemWrAddr := exu.io.oMemWrAddr
    amu.io.iMemWrData := exu.io.oMemWrData

    mem.io.iMemWrEn   := amu.io.oMemWrEn
    mem.io.iMemWrAddr := amu.io.oMemWrAddr
    mem.io.iMemWrData := amu.io.oMemWrData

    val wbu = Module(new WBU())
    ifu.io.iJmpEn     := exu.io.oJmpEn
    ifu.io.iPC        := exu.io.oPC
    wbu.io.iRegWrEn   := exu.io.oRegWrEn
    wbu.io.iRegWrAddr := exu.io.oRegWrAddr
    wbu.io.iRegWrData := exu.io.oRegWrData

    reg.io.iRegWrEn   := wbu.io.oRegWrEn
    reg.io.iRegWrAddr := wbu.io.oRegWrAddr
    reg.io.iRegWrData := wbu.io.oRegWrData

    val dpi = Module(new DPI())
    dpi.io.iEbreakFlag := Mux(idu.io.oALUType === ALU_TYPE_EBREAK, 1.U, 0.U)

    printf("pc:          0x%x\n", io.oPC)
    printf("inst:        0x%x\n", io.iInst)
    printf("rs1 addr:   %d\n", idu.io.oInstRS1Addr)
    printf("rs2 addr:   %d\n", idu.io.oInstRS2Addr)
    printf("rd  addr:   %d\n", idu.io.oInstRDAddr)
    printf("rs1 val :    0x%x\n", idu.io.iInstRS1Val)
    printf("rs2 val :    0x%x\n", idu.io.iInstRS2Val)
    printf("rd  val :    0x%x\n", exu.io.oRegWrData)
    printf("alu type: %d\n", idu.io.oALUType)
    printf("alu rs1 val: 0x%x\n", idu.io.oALURS1Val)
    printf("alu rs2 val: 0x%x\n", idu.io.oALURS2Val)
    printf("alu out:     0x%x\n", exu.io.oALUOut)
    printf("jmp en:      %d\n", idu.io.oJmpEn)
    printf("mem wr en:   %d\n", idu.io.oMemWrEn)
    printf("reg wr en:   %d\n", idu.io.oRegWrEn)
    printf("csr wr en:   %d\n\n", idu.io.oCsrWrEn)

    // val io = IO(new Bundle {
    //     val iInst      =  Input(UInt(32.W))
    //     val oPC        = Output(UInt(64.W))
    //     val oInstRDVal = Output(UInt(64.W))
    //     val oHalt      = Output(Bool())
    // });

    // val ifu = Module(new IFU())
    // val pc = RegInit("x80000000".U(64.W))
    // ifu.io.iPC := pc
    // io.oPC := ifu.io.oPC

    // val idu = Module(new IDU())
    // idu.io.iInst := io.iInst

    // val exu = Module(new EXU())
    // exu.io.iInstType := idu.io.oInstType
    // exu.io.iInstRS1  := idu.io.oInstRS1
    // exu.io.iInstRS2  := idu.io.oInstRS2
    // exu.io.iInstRD   := idu.io.oInstRD
    // exu.io.iInstImm  := idu.io.oInstImm

    // io.oInstRDVal := exu.io.oInstRDVal
    // io.oHalt      := exu.io.oHalt

    // val dpi = Module(new DPI())
    // dpi.io.iEbreakFlag := Mux(idu.io.oInstType === EBREAK.U, 1.U, 0.U)

    // printf("pc:   0x%x\n", io.oPC)
    // printf("inst: 0x00000000%x\n", io.iInst)
    // printf("res:  0x%x\n", io.oInstRDVal)
    // printf("halt: %x\n\n", io.oHalt)

    // pc := pc + 4.U
}
