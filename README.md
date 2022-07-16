> **Warning**
> This plugin is under development
> このプラグインは開発中です

# SakuraTrue🌸
Minecraft authentication plugin.  
Minecraftの認証プラグイン。


# Configuration
```yaml
verifier: discord # "discord" or "none"
discord:
  token: "" # Discord Bot Token
  guildId: "" # Discord server id
  commandDescriptions: # Command descriptions
    root: "SakuraTrue Command."
    verify: "Command to authenticate users within the Minecraft server."
  changeNickName: true # Whether to change the nickname when confirming the user.
  addRole: # Whether to add roles when confirming the user.
    enabled: false
    roles:
      - "000000000000000000" # Role id
```
