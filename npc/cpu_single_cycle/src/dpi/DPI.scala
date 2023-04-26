package cpu.dpi

import chisel3._;
import chisel3.util._

import cpu.util.Base._

class DPI extends BlackBox {
    val io = IO(new Bundle {
        val iEbreakFlag =  Input(UInt(ADDR_WIDTH.W))
        val iMemRdAddr  =  Input(UInt(DATA_WIDTH.W))
        val iMemWrEn    =  Input(Bool())
        val iMemWrAddr  =  Input(UInt(DATA_WIDTH.W))
        val iMemWrData  =  Input(UInt(DATA_WIDTH.W))
        val oMemRdData  = Output(UInt(DATA_WIDTH.W))
        // val iRegVal     =  Input(UInt(DATA_WIDTH.W))
        // val oRegAddr    = Output(UInt(DATA_WIDTH.W))
    })
}
