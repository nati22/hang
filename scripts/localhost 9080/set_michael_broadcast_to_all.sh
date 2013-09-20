URL=http://localhost:9080

# Broadcast to Dwight and Andy
CURL -XPUT -d target=5678 -d target=9101112 $URL/users/1234/broadcasts 