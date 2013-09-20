URL=http://localhost:9080

# Stop Broadcasting to Michael and Andy
CURL -XDELETE -d target=1234 -d target=9101112 $URL/users/5678/broadcasts -G