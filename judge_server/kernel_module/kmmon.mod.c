#include <linux/module.h>
#include <linux/vermagic.h>
#include <linux/compiler.h>

MODULE_INFO(vermagic, VERMAGIC_STRING);

struct module __this_module
__attribute__((section(".gnu.linkonce.this_module"))) = {
 .name = KBUILD_MODNAME,
 .init = init_module,
#ifdef CONFIG_MODULE_UNLOAD
 .exit = cleanup_module,
#endif
};

static const struct modversion_info ____versions[]
__attribute_used__
__attribute__((section("__versions"))) = {
	{ 0xe03465db, "struct_module" },
	{ 0x24b6595b, "wake_up_process" },
	{ 0xb2fd5ceb, "__put_user_4" },
	{ 0xce23e3a1, "page_address" },
	{ 0x8531de2f, "zone_table" },
	{ 0x1fb262c2, "get_user_pages" },
	{ 0x2e60bace, "memcpy" },
	{ 0x48b77c6b, "send_sig" },
	{ 0x4292364c, "schedule" },
	{ 0xe0a2a8e1, "send_sig_info" },
	{ 0xb65f9a3c, "find_task_by_pid_type" },
	{ 0x1b7d4074, "printk" },
};

static const char __module_depends[]
__attribute_used__
__attribute__((section(".modinfo"))) =
"depends=";

