default: all

all: .judged .zunit

clean:
	@make -C client clean
	@make -C zunit clean

install: all
	@chmod +x script/install.sh
	@script/install.sh

.judged: .zunit
	@make -C client

.zunit:
	@make -C zunit
