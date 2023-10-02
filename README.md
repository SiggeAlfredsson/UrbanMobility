# Spring boot assignment with focus on writing tests

- getting a booking broken? In the booking I get the user, and in the user I get the bookings again, and agian, and again, only reason the test passes is because it is not so much data. Would look into more of it if I werent so short on time


- I feel like i alot of logic way more complicated then it had to be, and then I still removed quite alot of logic in UserService.

- I was big time sick during this project, most of the project was made in the last 3 days.


--- RUNNING TESTS ---
 
 Running test locally thru jenkins:

Enter git repo URL in Source code management and branch to build, in my case testing.
build steps:

1 - .mvnw compile

2 - .mvnw package

If build is successful, it means the tests have passed locally.

One requirement was to run the tests in a ubuntu environment, Instead of going with docker (tested that also) I wanted to try to set up a EC2 instance.
I set up a ubuntu 22.04 LTS Server.
It was a little tricky to connect it with jenkins and set up a pipeline,

I made jenkins connect to the server via ssh and a pem key which i added into my jenkins credentials.
To create build steps in a ssh server looks a little different, 


![74caae8dd49c25810060b6296dad5ffa](https://github.com/SiggeAlfredsson/UrbanMobility/assets/113336400/de47528d-9fc3-4e4a-8ad4-736fa8add28b)

But it works just the same as previus build steps, I wanted to add a Post-build Action to push the testing branch into main if succeeds, but I never got it to work.

My EC2 server has Java 17 and Maven installed.
