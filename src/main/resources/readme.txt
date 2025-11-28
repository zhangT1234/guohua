工作流待办中间表创建语句

CREATE TABLE sec_dev_task_msg(
	phid varchar2(35) PRIMARY KEY,
	taskid varchar2(20),
	userid varchar2(20),
	status varchar2(8),
	url varchar2(1000),
	dbname varchar2(7),
	pushadd varchar2(1) DEFAULT '0',
	pushaddtime varchar2(25),
	pushdone varchar2(1) DEFAULT '0',
	pushdonetime varchar2(25),
	pushdel varchar2(1) DEFAULT '0',
	pushdeltime varchar2(25)
)


