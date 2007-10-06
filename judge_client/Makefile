all : .kernel_module .server

clean :
	@make -C kernel_module clean
	@make -C server clean

.kernel_module : 
	@make -C kernel_module

.server : .kernel_module
	@make -C server

