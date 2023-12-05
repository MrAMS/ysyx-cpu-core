package cpu.port

import chisel3._
import chisel3.util._

import cpu.common._

class IFUIO extends Bundle with ConfigIO {
    val oPC   = Output(UInt(DATA_WIDTH.W))
    val oInst = Output(UInt(DATA_WIDTH.W))
}
