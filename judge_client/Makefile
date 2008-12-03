default: all

all: .server .zunit

clean:
	@make -C server clean
	@make -C zunit clean

install: all
	@chmod +x script/install.sh
	@script/install.sh

.server: .zunit
	@make -C client

.zunit:
	@make -C zunit
