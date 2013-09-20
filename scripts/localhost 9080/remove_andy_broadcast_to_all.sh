URL=http://localhost:9080

# Stop Broadcasting to Michael and Dwight
CURL -XDELETE -d target=1234 -d target=5678 $URL/users/9101112/broadcasts -G


