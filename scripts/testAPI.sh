URL=http://hangapp2.appspot.com


# Create users
curl -XPUT -d fn=Girum -d ln=Ibssa $URL/users/563718987 # Create Girum
curl -XPUT -d fn=Nati -d ln=Tessema $URL/users/1029720625 # Create Nati
curl -XPUT -d fn=Samora -d ln=Deng $URL/users/538971239 # Create Samora

# Add new broadcast
curl -XPUT -d target=1029720625 $URL/users/563718987/broadcast # Girum -> Nati
curl -XPUT -d target=538971239 $URL/users/563718987/broadcast # Girum -> Samora

curl -XPUT -d target=563718987 $URL/users/1029720625/broadcast # Nati -> Girum
curl -XPUT -d target=538971239 $URL/users/1029720625/broadcast # Nati -> Samora

curl -XPUT -d target=563718987 $URL/users/538971239/broadcast # Samora -> Girum
curl -XPUT -d target=1029720625 $URL/users/538971239/broadcast # Samora -> Nati

# Create new status
curl -XPUT -d color=GREEN -d exp="28 Apr 2014 08:59:47 GMT" $URL/users/563718987/status # Set Girum's status
curl -XPUT -d color=RED -d exp"28 Apr 2014 08:59:47 GMT" $URL/users/1029720625/status # Set Nati's status

# Create new proposal
curl -XPUT -d des="basketball?" -d loc="the court by my place" -d time="28 Apr 2014 08:59:47 GMT" -d int=1029720625 -d int=538971239 -d conf=1029720625 $URL/users/563718987/proposal # Set Girum's proposal
curl -XPUT -d des="pop a molly" -d loc="my house" -d time="28 Apr 2014 08:59:47 GMT" -d int=563718987 -d int=538971239 -d conf=563718987 $URL/users/1029720625/proposal # Set Nati's proposal

curl -XDELETE $URL/users/1029720625/proposal
curl -XPUT -d des="pop a molly" -d loc="my house" -d time="28 Apr 2014 08:59:47 GMT" -d int=563718987 -d int=538971239 -d conf=563718987 $URL/users/1029720625/proposal # Set Nati's proposal