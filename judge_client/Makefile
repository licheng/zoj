default: all

all: .kernel_module .server

clean:
	@make -C kernel_module clean
	@make -C server clean

install: all
	@chmod +x script/install.sh
	@script/install.sh

.kernel_module:
	@make -C kernel_module

.server: .kernel_module
	@make -C client

