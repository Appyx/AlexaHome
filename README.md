# AlexaHome
The easy way to create Alexa Smart Home Skills (MTLS secured).


### What is it?
This project enables you to develop a [Smart Home Skill](https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/overviews/understanding-the-smart-home-skill-api) for Amazon Echo.
This is a special type of Skill where you don't need to speak out the skillname. You just say something like *"Alexa turn on the Lights"*.
The Amazon API does all the word processing for you and activates the right Smart Device. 

AlexaHome is not a Smart Home Skill, it's a Framework for developing Smart Home Skills.

Let's compare two ways of writing Smart Home Skills:

### Without AlexaHome
In order to develop a Smart Home Skill you need the following things:

* [AWS Account](https://aws.amazon.com) (which in turn requires a credit card)
* [Amazon Developer Account](https://developer.amazon.com) (this one is free)
* [AWS Lambda Function](https://aws.amazon.com/lambda/details/)
* [OAuth](https://en.wikipedia.org/wiki/OAuth) Provider (it's not possible to skip this part)
* [TLS](https://en.wikipedia.org/wiki/Transport_Layer_Security) encryption between the AWS Lambda Function and your device
* Some form of authentication for your Lambda Function and your Device (to prevent MITM attacks)
* Know how to use the JSON API of Amazon
* Write a lot of code for each device.

And there is more...


### With AlexaHome
AlexaHome solves not all, but most of the problems described above.

* [AWS Account](https://aws.amazon.com)
* [Amazon Developer Account](https://developer.amazon.com)
* Write simple Java/Kotlin code with well defined interfaces


### Features

You also get features which you won't get with the 'blank' Amazon API.

* Mutual TLS between all components (all parties prove their identity).
* Share one AWS account with multiple developer accounts (only one credit card is needed)
* Use multiple Echos at multiple locations with multiple devices at multiple locations
* No need to write an AWS Lambda Function
* No need to compile or clone anything related with AlexaHome
* Automatic error handling (Alexa responds with the right errors by default)
* Host the actual Skill anywhere you want (home,remote server,amazon...).
* Ongoing development under the hood. (So once a new API is available you just use other interfaces in your Java/Kotlin code)

And all is free (you still one credit card for the AWS account, but there is a free tier which should last a while)


### How to start?

Take a look at the [Wiki](https://github.com/Appyx/AlexaHome/wiki) for instructions.

### Contributions

Please open a pull request at the [AlexaHomePlugins](https://github.com/Appyx/AlexaHomePlugins) repository when you developed a plugin which can be used by other people.

And of course, other pull requests are welcome :)


