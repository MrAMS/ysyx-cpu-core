package cpu.comp

import chisel3._
import chisel3.util._

import cpu.util.Base._

class RegM extends Module {
    val io = IO(new Bundle {
        val iRegWrEn      = Input(Bool())
        val iRegRd1Addr   = Input(UInt(DATA_WIDTH.W))
        val iRegRd2Addr   = Input(UInt(DATA_WIDTH.W))
        val iRegRdEndAddr = Input(UInt(DATA_WIDTH.W))
        val iRegRdSdbAddr = Input(UInt(DATA_WIDTH.W))
        val iRegWrAddr    = Input(UInt(DATA_WIDTH.W))
        val iRegWrData    = Input(UInt(DATA_WIDTH.W))

        val iRegAddr0  = Input(UInt(DATA_WIDTH.W))
        val iRegAddr1  = Input(UInt(DATA_WIDTH.W))
        val iRegAddr2  = Input(UInt(DATA_WIDTH.W))
        val iRegAddr3  = Input(UInt(DATA_WIDTH.W))
        val iRegAddr4  = Input(UInt(DATA_WIDTH.W))
        val iRegAddr5  = Input(UInt(DATA_WIDTH.W))
        val iRegAddr6  = Input(UInt(DATA_WIDTH.W))
        val iRegAddr7  = Input(UInt(DATA_WIDTH.W))
        val iRegAddr8  = Input(UInt(DATA_WIDTH.W))
        val iRegAddr9  = Input(UInt(DATA_WIDTH.W))
        val iRegAddr10 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr11 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr12 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr13 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr14 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr15 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr16 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr17 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr18 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr19 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr20 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr21 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr22 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr23 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr24 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr25 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr26 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr27 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr28 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr29 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr30 = Input(UInt(DATA_WIDTH.W))
        val iRegAddr31 = Input(UInt(DATA_WIDTH.W))

        val oRegRd1Data   = Output(UInt(DATA_WIDTH.W))
        val oRegRd2Data   = Output(UInt(DATA_WIDTH.W))
        val oRegRdEndData = Output(UInt(DATA_WIDTH.W))
        val oRegRdSdbData = Output(UInt(DATA_WIDTH.W))

        val oRegData0  = Output(UInt(DATA_WIDTH.W))
        val oRegData1  = Output(UInt(DATA_WIDTH.W))
        val oRegData2  = Output(UInt(DATA_WIDTH.W))
        val oRegData3  = Output(UInt(DATA_WIDTH.W))
        val oRegData4  = Output(UInt(DATA_WIDTH.W))
        val oRegData5  = Output(UInt(DATA_WIDTH.W))
        val oRegData6  = Output(UInt(DATA_WIDTH.W))
        val oRegData7  = Output(UInt(DATA_WIDTH.W))
        val oRegData8  = Output(UInt(DATA_WIDTH.W))
        val oRegData9  = Output(UInt(DATA_WIDTH.W))
        val oRegData10 = Output(UInt(DATA_WIDTH.W))
        val oRegData11 = Output(UInt(DATA_WIDTH.W))
        val oRegData12 = Output(UInt(DATA_WIDTH.W))
        val oRegData13 = Output(UInt(DATA_WIDTH.W))
        val oRegData14 = Output(UInt(DATA_WIDTH.W))
        val oRegData15 = Output(UInt(DATA_WIDTH.W))
        val oRegData16 = Output(UInt(DATA_WIDTH.W))
        val oRegData17 = Output(UInt(DATA_WIDTH.W))
        val oRegData18 = Output(UInt(DATA_WIDTH.W))
        val oRegData19 = Output(UInt(DATA_WIDTH.W))
        val oRegData20 = Output(UInt(DATA_WIDTH.W))
        val oRegData21 = Output(UInt(DATA_WIDTH.W))
        val oRegData22 = Output(UInt(DATA_WIDTH.W))
        val oRegData23 = Output(UInt(DATA_WIDTH.W))
        val oRegData24 = Output(UInt(DATA_WIDTH.W))
        val oRegData25 = Output(UInt(DATA_WIDTH.W))
        val oRegData26 = Output(UInt(DATA_WIDTH.W))
        val oRegData27 = Output(UInt(DATA_WIDTH.W))
        val oRegData28 = Output(UInt(DATA_WIDTH.W))
        val oRegData29 = Output(UInt(DATA_WIDTH.W))
        val oRegData30 = Output(UInt(DATA_WIDTH.W))
        val oRegData31 = Output(UInt(DATA_WIDTH.W))
    })

    val regFile = Mem(REG_NUM, UInt(DATA_WIDTH.W))

    when (io.iRegWrEn) {
        regFile(io.iRegWrAddr) := io.iRegWrData
    }

    regFile(0.U) := 0.U(DATA_WIDTH.W)

    io.oRegRd1Data   := regFile(io.iRegRd1Addr)
    io.oRegRd2Data   := regFile(io.iRegRd2Addr)
    io.oRegRdEndData := regFile(io.iRegRdEndAddr)
    io.oRegRdSdbData := regFile(io.iRegRdSdbAddr)

    io.oRegData0  := regFile(io.iRegAddr0)
    io.oRegData1  := regFile(io.iRegAddr1)
    io.oRegData2  := regFile(io.iRegAddr2)
    io.oRegData3  := regFile(io.iRegAddr3)
    io.oRegData4  := regFile(io.iRegAddr4)
    io.oRegData5  := regFile(io.iRegAddr5)
    io.oRegData6  := regFile(io.iRegAddr6)
    io.oRegData7  := regFile(io.iRegAddr7)
    io.oRegData8  := regFile(io.iRegAddr8)
    io.oRegData9  := regFile(io.iRegAddr9)
    io.oRegData10 := regFile(io.iRegAddr10)
    io.oRegData11 := regFile(io.iRegAddr11)
    io.oRegData12 := regFile(io.iRegAddr12)
    io.oRegData13 := regFile(io.iRegAddr13)
    io.oRegData14 := regFile(io.iRegAddr14)
    io.oRegData15 := regFile(io.iRegAddr15)
    io.oRegData16 := regFile(io.iRegAddr16)
    io.oRegData17 := regFile(io.iRegAddr17)
    io.oRegData18 := regFile(io.iRegAddr18)
    io.oRegData19 := regFile(io.iRegAddr19)
    io.oRegData20 := regFile(io.iRegAddr20)
    io.oRegData21 := regFile(io.iRegAddr21)
    io.oRegData22 := regFile(io.iRegAddr22)
    io.oRegData23 := regFile(io.iRegAddr23)
    io.oRegData24 := regFile(io.iRegAddr24)
    io.oRegData25 := regFile(io.iRegAddr25)
    io.oRegData26 := regFile(io.iRegAddr26)
    io.oRegData27 := regFile(io.iRegAddr27)
    io.oRegData28 := regFile(io.iRegAddr28)
    io.oRegData29 := regFile(io.iRegAddr29)
    io.oRegData30 := regFile(io.iRegAddr30)
    io.oRegData31 := regFile(io.iRegAddr31)
}
