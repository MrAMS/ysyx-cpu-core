package cpu.port

import chisel3._
import chisel3.util._

import cpu.common._

class AXI4LiteARIO extends Bundle with ConfigIO {
    val addr = Output(UInt(DATA_WIDTH.W))
}

class AXI4LiteRIO extends Bundle with ConfigIO {
    val data = Output(UInt(DATA_WIDTH.W))
    val resp = Output(UInt(RESP_WIDTH.W))
}

class AXI4LiteAWIO extends Bundle with ConfigIO {
    val addr = Output(UInt(DATA_WIDTH.W))
}

class AXI4LiteWIO extends Bundle with ConfigIO {
    val data = Output(UInt(DATA_WIDTH.W))
    val strb = Output(UInt((DATA_WIDTH / BYTE_WIDTH).W))
}

class AXI4LiteBIO extends Bundle with ConfigIO {
    val resp = Output(UInt(RESP_WIDTH.W))
}

class AXI4LiteIO extends Bundle with ConfigIO {
    val ar = Decoupled(new AXI4LiteARIO)
    val r  = Flipped(Decoupled(new AXI4LiteRIO))
    val aw = Decoupled(new AXI4LiteAWIO)
    val w  = Decoupled(new AXI4LiteWIO)
    val b  = Flipped(Decoupled(new AXI4LiteBIO))
}
