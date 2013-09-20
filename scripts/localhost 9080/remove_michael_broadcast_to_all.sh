URL=http://localhost:9080

# Stop broadcasting to Dwight and Andy
CURL -XDELETE -d target=5678 d target=9101112 $URL/users/1234/broadcasts -G
