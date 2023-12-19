`include "Config.v"

module AXI4LiteS(
    input  wire                       iClock,
    input  wire                       iReset,
    input  wire [`MODE_WIDTH - 1 : 0] iMode,
    input  wire [`DATA_WIDTH - 1 : 0] iData,
    input  wire [`RESP_WIDTH - 1 : 0] iResp,
    output wire [`ADDR_WIDTH - 1 : 0] oAddr,
    output wire [`DATA_WIDTH - 1 : 0] oData,
    output wire [`MASK_WIDTH - 1 : 0] oMask,

    input  wire                       pAXI4_ar_valid,
    input  wire [`ADDR_WIDTH - 1 : 0] pAXI4_ar_bits_addr,
    output wire                       pAXI4_ar_ready,

    input  wire                       pAXI4_r_ready,
    output wire                       pAXI4_r_valid,
    output wire [`DATA_WIDTH - 1 : 0] pAXI4_r_bits_data,
    output wire [`RESP_WIDTH - 1 : 0] pAXI4_r_bits_resp,

    input  wire                       pAXI4_aw_valid,
    input  wire [`ADDR_WIDTH - 1 : 0] pAXI4_aw_bits_addr,
    output wire                       pAXI4_aw_ready,

    input  wire                       pAXI4_w_valid,
    input  wire [`DATA_WIDTH - 1 : 0] pAXI4_w_bits_data,
    input  wire [`MASK_WIDTH - 1 : 0] pAXI4_w_bits_strb,
    output wire                       pAXI4_w_ready,

    input  wire                       pAXI4_b_ready,
    output wire                       pAXI4_b_valid,
    output wire [`RESP_WIDTH - 1 : 0] pAXI4_b_bits_resp
);

    //-------------------------------------------------------------------------
    wire w_rd_a_active;
    wire w_rd_d_active;
    wire w_wr_a_active;
    wire w_wr_d_active;
    wire w_wr_b_active;

    //-------------------------------------------------------------------------
    reg                       r_arready;
    reg [`ADDR_WIDTH - 1 : 0] r_araddr;
    reg                       r_rvalid;
    reg                       r_awready;
    reg [`ADDR_WIDTH - 1 : 0] r_awaddr;
    reg                       r_wready;
    reg                       r_bvalid;

    reg [`ADDR_WIDTH - 1 : 0] r_addr;
    reg [`RESP_WIDTH - 1 : 0] r_rd_resp;
    reg [`RESP_WIDTH - 1 : 0] r_wr_resp;

    //-------------------------------------------------------------------------
    assign oAddr             = r_addr;

    assign pAXI4_ar_ready    = r_arready;
    assign pAXI4_r_valid     = r_rvalid;
    assign pAXI4_r_bits_data = iData;
    assign pAXI4_r_bits_resp = r_rd_resp;
    assign pAXI4_aw_ready    = r_awready;
    assign pAXI4_w_ready     = r_wready;
    assign pAXI4_b_valid     = r_bvalid;
    assign pAXI4_b_bits_resp = r_wr_resp;

    assign w_rd_a_active = pAXI4_ar_valid && pAXI4_ar_ready;
    assign w_rd_d_active = pAXI4_r_valid  && pAXI4_r_ready;
    assign w_wr_a_active = pAXI4_aw_valid && pAXI4_aw_ready;
    assign w_wr_d_active = pAXI4_w_valid  && pAXI4_w_ready;
    assign w_wr_b_active = pAXI4_b_valid  && pAXI4_b_ready;

    // assign oAddr = (iMode === `MODE_RD) ? r_araddr : r_awaddr;
    assign oData = (w_wr_d_active) ? pAXI4_w_bits_data : `DATA_WIDTH'b0;
    assign oMask = (w_wr_d_active) ? pAXI4_w_bits_strb : `MASK_WIDTH'b0;
    always @(*) begin
        if (iMode === `MODE_RD) begin
            r_addr = r_araddr;
            r_rd_resp = iResp;
        end
        else if (iMode === `MODE_WR) begin
            r_addr = r_awaddr;
            r_wr_resp = iResp;
        end
        else if (iMode === `MODE_RW) begin
            if (w_rd_a_active && !w_wr_a_active) begin
                r_addr = r_araddr;
            end
            else if (!w_rd_a_active && w_wr_a_active) begin
                r_addr = r_awaddr;
            end
            else begin
                r_addr = r_addr;
            end

            if (w_rd_d_active && !w_wr_b_active) begin
                r_rd_resp = iResp;
            end
            else if (!w_rd_d_active && w_wr_b_active) begin
                r_wr_resp = iResp;
            end
            else begin
                r_rd_resp = r_rd_resp;
                r_wr_resp = r_wr_resp;
            end
        end
        else begin
            r_addr = r_addr;
            r_rd_resp = r_rd_resp;
            r_wr_resp = r_wr_resp;
        end
    end

    //-------------------------------------------------------------------------
    always @(posedge iClock) begin
        if (iReset) begin
            r_arready <= 1'b1;
        end
        else if (w_rd_a_active) begin
            r_arready <= 1'b0;
        end
        else if (w_rd_d_active) begin
            r_arready <= 1'b1;
        end
        else begin
            r_arready <= r_arready;
        end
    end

    always @(posedge iClock) begin
        if (iReset) begin
            r_araddr <= `ADDR_WIDTH'b0;
        end
        else if (w_rd_a_active) begin
            r_araddr <= pAXI4_ar_bits_addr;
        end
        else if (w_rd_d_active) begin
            r_araddr <= `ADDR_WIDTH'b0;
        end
        else begin
            r_araddr <= r_araddr;
        end
    end

    always @(posedge iClock) begin
        if (iReset) begin
            r_rvalid <= 1'b0;
        end
        else if (w_rd_a_active) begin
            r_rvalid <= 1'b1;
        end
        else if (w_rd_d_active) begin
            r_rvalid <= 1'b0;
        end
        else begin
            r_rvalid <= r_rvalid;
        end
    end

    always @(posedge iClock) begin
        if (iReset) begin
            r_awready <= 1'b1;
        end
        else if (w_wr_a_active) begin
            r_awready <= 1'b0;
        end
        else if (w_wr_d_active) begin
            r_awready <= 1'b1;
        end
        else begin
            r_awready <= r_awready;
        end
    end

    always @(posedge iClock) begin
        if (iReset) begin
            r_awaddr <= `ADDR_WIDTH'b0;
        end
        else if (w_wr_a_active) begin
            r_awaddr <= pAXI4_ar_bits_addr;
        end
        else if (w_wr_d_active) begin
            r_awaddr <= `ADDR_WIDTH'b0;
        end
        else begin
            r_awaddr <= r_awaddr;
        end
    end

    always @(posedge iClock) begin
        if (iReset) begin
            r_wready <= 1'b0;
        end
        else if (w_wr_a_active) begin
            r_wready <= 1'b1;
        end
        else if (w_wr_d_active) begin
            r_wready <= 1'b0;
        end
        else begin
            r_wready <= r_wready;
        end
    end

    always @(posedge iClock) begin
        if (iReset) begin
            r_bvalid <= 1'b0;
        end
        else if (w_wr_d_active) begin
            r_bvalid <= 1'b1;
        end
        else if (w_wr_b_active) begin
            r_bvalid <= 1'b0;
        end
        else begin
            r_bvalid <= r_bvalid;
        end
    end

endmodule
