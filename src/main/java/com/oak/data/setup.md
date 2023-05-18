# Installation
1. use `sudo apt-get install mysql-server` tp install mysql.
2. use `sudo systemctl start mysql` to start up the database
3. use `sudo mysql` to start up the cli
4. Add the new user 'admin':
5. `CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';`
6. `GRANT ALL PRIVILEGES ON *.* TO 'admin'@'localhost';`
7. You are done!
# Setup
1. Add the dependencies(mysql and dbcp) to pom.xml.
2. Add ConnectionPool class
3. You are done!