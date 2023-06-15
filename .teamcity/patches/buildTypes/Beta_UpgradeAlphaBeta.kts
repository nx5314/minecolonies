package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Beta_UpgradeAlphaBeta'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Beta_UpgradeAlphaBeta")) {
    check(name == "Upgrade - Alpha -> Beta") {
        "Unexpected name: '$name'"
    }
    name = "Upgrade - Beta -> Release"
}
