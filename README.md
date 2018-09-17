Nexus Repository Manager Poller Plugin for GoCD
============================

Introduction
------------
This is a [package material](https://docs.go.cd/current/extension_points/package_repository_extension.html) plugin for [GoCD](http://www.go.cd/). It is currently capable of polling [Nexus](https://www.sonatype.com/nexus-repository-sonatype) repositories.

The behaviour and capabilities of the plugin is determined to a significant extent by that of the package material extension point in GoCD. Be sure to read the package material documentation before using this plugin.

Installation
------------
Just drop [go-nexus-poller.jar](https://github.com/BenWolstencroft/go-nexus-poller-plugin/releases) into plugins/external directory and restart GoCD. More details [here](https://docs.go.cd/current/extension_points/plugin_user_guide.html)

Compatibility
------------
This plugin is compatible with the JSON message based plugin API introduced in version 14.4.0. More details [here](https://developer.go.cd/16.12.0/writing_go_plugins/json_message_based_plugin_api.html)

Repository definition
---------------------
![Add a NuGet repository][1]

Nexus Server URL must be a valid http or https URL. The plugin will try to access URL/services/rest/v1/repositories to report successful connection. Basic authentication (user:password@host/path) is supported. We recommend only using authentication over HTTPS.

Package definition
------------------
Click check package to make sure the plugin understands what you are looking for. Note that the version constraints are ANDed if both are specified.

![Define a package as material for a pipeline][2]

[1]: img/add-nexus-repo.png  "Define Nexus Package Repository"
[2]: img/add-nexus-package.png  "Define package as material for a pipeline"
