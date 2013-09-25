URL=http://hangapp2.appspot.com

# Not interested in Broadcast for Michael
#CURL -XDELETE -d target=1234 $URL/users/9101112/proposal/interested -G
# Interested in Broadcast for Andy
CURL -XDELETE -d target=1029720625 $URL/users/100005943390385/proposal/interested -G