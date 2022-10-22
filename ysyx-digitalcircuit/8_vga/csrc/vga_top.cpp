#include <nvboard.h>
#include "Vvga_top.h"

static TOP_NAME dut;

void nvboard_bind_all_pins(Vvga_top* vga_top);

static void simple_cycle() {
    dut.i_clk = 0;
    dut.eval();
    dut.i_clk = 1;
    dut.eval();
}

static void reset(int n) {
    dut.i_clr_n = 0;
    while (n-- > 0) {
    }
    dut.i_clr_n = 1;
}

int main() {
    nvboard_bind_all_pins(&dut);
    nvboard_init();

    reset(10);

    while(1) {
        nvboard_update();
        simple_cycle();
    }

    nvboard_quit();
}
