import sys
sys.stdout.reconfigure(encoding='utf-8')
path = 'D:\\IDEA\\shusixue\\shusixue-backend\\src\\main\\resources\\application.yml'
with open(path, 'rb') as f:
    data = f.read()
text = data.decode('utf-8')

# Replace datasource with env vars
old_url = "url: jdbc:mysql://localhost:3306/shusixue?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
new_url = "url: jdbc:mysql://:/?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&useSSL=true&requireSSL=false&allowPublicKeyRetrieval=true"
text = text.replace(old_url, new_url)
text = text.replace('username: root', 'username: ')
text = text.replace('password: fkz520520', 'password: ')
text = text.replace('host: localhost', 'host: ')
text = text.replace('port: 6379', 'port: ')

with open(path, 'wb') as f:
    f.write(text.encode('utf-8'))
print('application.yml updated')
