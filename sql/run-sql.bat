npm@echo off

REM 创建数据库
mysql -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS springboot-vue DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

REM 执行SQL脚本
mysql -u root -p123456 springboot-vue < office-learning.sql

echo SQL脚本执行完成！
pause