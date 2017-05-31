inherit extrausers

EXTRA_USERS_PARAMS = " \
	usermod -P root123 root; \
	useradd -P fpga123 fpga; "
