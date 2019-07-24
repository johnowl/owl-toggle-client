# Client for [feature toggle server](https://github.com/johnowl/owl-toggle-server)

The feature toggle client has two methods

- `sendVariables(userId: String, variables: Map<String, Any>): Unit`
- `isEnabled(featureToggleId: String, userId: String, defaultValue: Boolean): Boolean`

And it needs a configuration `owl.toggle.client.server-url` with the url of the feature toggle server.

## The sendVariables method

It's used to feed the server with variables that can be used to evaluate the rules. A common cenario of use it's to 
call this method on every user login. The first parameter is the user id and the second one store the variables, it's is 
of type `Map<String, Any>`. Example of use:

    @RestController
    class LoginController {

        private val loginService: LoginService
        private val featureToggleClient: FeatureToggleClient

        @Autowired
        constructor(loginService: LoginService, featureToggleClient: FeatureToggleClient) {
            this.loginService = loginService
            this.featureToggleClient = featureToggleClient
        }

        @PostMapping("/login")
        fun login(@RequestBody login: Login, @RequestHeader("client-version") version: Int): AccessToken {

            val loginResponse = loginService.login(login.username, login.password)

            if(loginResponse.success) {
                featureToggleClient.sendVariables(login.username, mapOf("appVersion" to version))
                return loginResponse.accessToken
            }

            throw InvalidCredentialsExcpetion()

        }

    }

## The isEnabled method

It's were the magic happens! It's the method used to check if a feature toggle is enabled or disabled. It has 3 parameters, 
the first one is the feature toggle identification, the second is the user id and the third one is an optional parameter with 
a default value to return if the feature toggle doesn't exist. Example of use:

  @Service
  class TimelineService {

      private val featureToggleClient: FeatureToggleClient
      private const val FEATURE_TOGGLE_ID = "my_toggle"

      @Autowired
      constructor(featureToggleClient: FeatureToggleClient) {
          this.featureToggleClient = featureToggleClient
      }


      fun doSomeWork(userId: String) {

          if(featureToggleClient.isEnabled(FEATURE_TOGGLE_ID, userId, false)) {
              doWorkThisWay()
          } else {
              doWorkThatWay()
          }
      }

      [...]
    }
