package cpu.stage

import chisel3._
import chisel3.util._

import cpu.util.Base._
import cpu.util.Inst._

class IDU extends Module {
    val io = IO(new Bundle {
        val iInst        = Input(UInt(DATA_WIDTH.W))
        val iInstRS1Val  = Input(UInt(DATA_WIDTH.W))
        val iInstRS2Val  = Input(UInt(DATA_WIDTH.W))
        val iPC          = Input(UInt(DATA_WIDTH.W))

        val oInstRS1Addr = Output(UInt(REG_WIDTH.W))
        val oInstRS2Addr = Output(UInt(REG_WIDTH.W))
        val oInstRDAddr  = Output(UInt(REG_WIDTH.W))
        val oInstRS1Val  = Output(UInt(DATA_WIDTH.W))
        val oInstRS2Val  = Output(UInt(DATA_WIDTH.W))
        val oInstName    = Output(UInt(SIGNAL_WIDTH.W))
        val oALUType     = Output(UInt(SIGNAL_WIDTH.W))
        val oALURS1Val   = Output(UInt(DATA_WIDTH.W))
        val oALURS2Val   = Output(UInt(DATA_WIDTH.W))
        val oJmpEn       = Output(Bool())
        val oMemWrEn     = Output(Bool())
        val oMemByt      = Output(UInt(SIGNAL_WIDTH.W))
        val oRegWrEn     = Output(Bool())
        val oRegWrSrc    = Output(UInt(SIGNAL_WIDTH.W))
    })

    val inst = io.iInst;
    var signals = ListLookup(
        inst,
        List(INST_NAME_X, ALU_TYPE_X, ALU_RS1_X, ALU_RS2_X, JMP_F, MEM_WR_F, MEM_BYT_X, REG_WR_F, REG_WR_SRC_X),
        Array(
            ADDI   -> List(INST_NAME_ADDI,   ALU_TYPE_ADD,    ALU_RS1_R,  ALU_RS2_IMM_I, JMP_F, MEM_WR_F, MEM_BYT_X, REG_WR_T, REG_WR_SRC_ALU),
            LUI    -> List(INST_NAME_LUI,    ALU_TYPE_ADD,    ALU_RS1_X,  ALU_RS2_IMM_U, JMP_F, MEM_WR_F, MEM_BYT_X, REG_WR_T, REG_WR_SRC_ALU),
            AUIPC  -> List(INST_NAME_AUIPC,  ALU_TYPE_ADD,    ALU_RS1_PC, ALU_RS2_IMM_U, JMP_F, MEM_WR_F, MEM_BYT_X, REG_WR_T, REG_WR_SRC_ALU),

            JAL    -> List(INST_NAME_JAL,    ALU_TYPE_ADD,    ALU_RS1_PC, ALU_RS2_IMM_J, JMP_T, MEM_WR_F, MEM_BYT_X, REG_WR_T, REG_WR_SRC_PC),
            JALR   -> List(INST_NAME_JALR,   ALU_TYPE_JALR,   ALU_RS1_R,  ALU_RS2_IMM_I, JMP_T, MEM_WR_F, MEM_BYT_X, REG_WR_T, REG_WR_SRC_PC),


            SB     -> List(INST_NAME_SB,     ALU_TYPE_ADD,    ALU_RS1_R,  ALU_RS2_IMM_S, JMP_F, MEM_WR_T, MEM_BYT_1, REG_WR_F, REG_WR_SRC_X),
            SH     -> List(INST_NAME_SH,     ALU_TYPE_ADD,    ALU_RS1_R,  ALU_RS2_IMM_S, JMP_F, MEM_WR_T, MEM_BYT_2, REG_WR_F, REG_WR_SRC_X),
            SW     -> List(INST_NAME_SW,     ALU_TYPE_ADD,    ALU_RS1_R,  ALU_RS2_IMM_S, JMP_F, MEM_WR_T, MEM_BYT_4, REG_WR_F, REG_WR_SRC_X),
            SD     -> List(INST_NAME_SD,     ALU_TYPE_ADD,    ALU_RS1_R,  ALU_RS2_IMM_S, JMP_F, MEM_WR_T, MEM_BYT_8, REG_WR_F, REG_WR_SRC_X),


            EBREAK -> List(INST_NAME_EBREAK, ALU_TYPE_EBREAK, ALU_RS1_X,  ALU_RS2_X,     JMP_F, MEM_WR_F, MEM_BYT_X, REG_WR_F, REG_WR_SRC_X))
    )
    val instName = signals(0)
    val aluType  = signals(1)
    val aluRS1   = signals(2)
    val aluRS2   = signals(3)
    val jmpEn    = signals(4)
    val memWrEn  = signals(5)
    val memByt   = signals(6)
    val regWrEn  = signals(7)
    val regWrSrc = signals(8)

    when (aluType === ALU_TYPE_X) {
        assert(false.B, "Invalid instruction at 0x%x", io.iPC)
    }

    val instRS1Addr  = inst(19, 15)
    val instRS2Addr  = inst(24, 20)
    val instRDAddr   = inst(11, 7)
    val instImmI     = inst(31, 20)
    val instImmISext = Cat(Fill(52, instImmI(11)), instImmI)
    val instImmS     = Cat(inst(31, 25), inst(11, 7))
    val instImmSSext = Cat(Fill(52, instImmS(11)), instImmS)
    val instImmU     = inst(31, 12)
    val instImmUSext = Cat(Fill(32, instImmU(19)), instImmU, 0.U(12.U))
    val instImmJ     = Cat(inst(31), inst(19, 12), inst(20), inst(30, 21))
    val instImmJSext = Cat(Fill(43, instImmJ(19)), instImmJ, 0.U(1.U))

    io.oInstRS1Addr := instRS1Addr
    io.oInstRS2Addr := instRS2Addr

    val aluRS1Val = MuxCase(
        0.U(DATA_WIDTH.W),
        Seq(
            (aluRS1 === ALU_RS1_X)  -> 0.U(DATA_WIDTH.W),
            (aluRS1 === ALU_RS1_R)  -> io.iInstRS1Val,
            (aluRS1 === ALU_RS1_PC) -> io.iPC
        )
    )
    val aluRS2Val = MuxCase(
        0.U(DATA_WIDTH.W),
        Seq(
            (aluRS2 === ALU_RS2_X)     -> 0.U(DATA_WIDTH.W),
            (aluRS2 === ALU_RS2_R)     -> io.iInstRS2Val,
            (aluRS2 === ALU_RS2_IMM_I) -> instImmISext,
            (aluRS2 === ALU_RS2_IMM_S) -> instImmSSext,
            (aluRS2 === ALU_RS2_IMM_U) -> instImmUSext,
            (aluRS2 === ALU_RS2_IMM_J) -> instImmJSext
        )
    )

    io.oInstRDAddr := instRDAddr
    io.oInstRS1Val := io.iInstRS1Val
    io.oInstRS2Val := io.iInstRS2Val
    io.oInstName   := instName
    io.oALUType    := aluType
    io.oALURS1Val  := aluRS1Val
    io.oALURS2Val  := aluRS2Val
    io.oJmpEn      := jmpEn
    io.oMemWrEn    := memWrEn
    io.oMemByt     := memByt
    io.oRegWrEn    := regWrEn
    io.oRegWrSrc   := regWrSrc
}
