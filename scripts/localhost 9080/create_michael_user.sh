URL=http://localhost:9080

# Create Michael Scott user from Facebook
curl -XPUT -d fn=Michael -d ln=Scott -d regid=002 $URL/users/1234