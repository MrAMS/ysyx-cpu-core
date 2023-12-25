`define VTRACE_MEMORY
`define VTRACE_MONITOR
`define VTRACE_AXI4_RD
// `define VTRACE_AXI4_WR

`define RESP_WIDTH 2
`define MODE_WIDTH 2
`define BYTE_WIDTH 8
`define SIGS_WIDTH 10
`define MEMS_WIDTH 16
`define INST_WIDTH 32
`define ADDR_WIDTH 64
`define DATA_WIDTH 64
`define MASK_WIDTH `DATA_WIDTH / `BYTE_WIDTH

`define MEMS_NUM 1 << `MEMS_WIDTH
`define ADDR_INIT 64'h80000000

`define DATA_ZERO = 1'd0

`define MEM_BYT_1_U 10'd1
`define MEM_BYT_2_U 10'd2
`define MEM_BYT_4_U 10'd3
`define MEM_BYT_8_U 10'd4

`define RESP_OKEY   2'd0
`define RESP_EXOKAY 2'd1
`define RESP_SLVEER 2'd2
`define RESP_DECEER 2'd3

`define MODE_RD 2'd0
`define MODE_WR 2'd1
`define MODE_RW 2'd2
