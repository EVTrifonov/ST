///
definition(
    name: "Enhanced Smart Lock",
    namespace: "evtrifonov",
    author: "Evgeny Trifonov",
    category: "Safety & Security",
    description: "Automatically locks/unlocks selected lock by contact sensor state and send notifications",
    iconUrl: "https://img.icons8.com/dotty/80/000000/lock-orientation.png",
    iconX2Url: "https://img.icons8.com/dotty/90/000000/lock-orientation.png",
    iconX3Url: "https://img.icons8.com/dotty/100/000000/lock-orientation.png",
    pausable: false)
{
    appSetting "maxLockUnlockAttempts"
    appSetting "lockUnlockAttemptDelay"
    appSetting "lockActualStateCheckDelay"
}

// version info
private def getVersion()
{
    return "1.0.6"
}

private def getModificationDate()
{
    return new Date().parse("d/M/yyyy", "12/12/2018")
}

// resources
def getSupportedLanguages()
{
    return ["EN": "English", "RU": "Русский (Russian)"]
}

private def getPreferencesResources(language = null)
{
    switch (getLanguageOrSelected(language))
    {
        case "EN": return [
            Version: "Version",
            Preferences: "Preferences",
            Interface: "Interface",
            Sources: "Sources",
            Actions: "Actions",
            Notifications: "Notifications",
            Other: "Other",
            SecondsPrompt: "After this number of seconds:",
            MinutesPrompt: "After this number of minutes:",
            Whichlock: "Lock:",
            WhichContactSensor: "Contact sensor:",
            LockWhenClosed: "Lock when closed?",
            UnlockWhenOpened: "Unlock when opened?",
            ReLockIfClosed: "Re-lock if left closed and unlocked?",
            LowBatteryNotifications: "Send low battery notifications?",
            BatteryWarningLevel: "Warn if lower than:",
            BatteryCriticalLevel: "Critical if lower than:",
            RepeatLowBatteryNotifications: "Repeat low battery notifications?",
            OpenCloseNotifications: "Send open/close notifications?",
            LockUnlockNotifications: "Send lock/unlock notifications?",
            NotifyLeftOpen: "Notify left open?",
            LabelPrompt: "Application name:",
            Language: "Language:"]

        case "RU": return [
            Version: "Версия",
            Preferences: "Настройки",
            Interface: "Интерфейс",
            Sources: "Устройства",
            Actions: "Действия",
            Notifications: "Уведомления",
            Other: "Разное",
            SecondsPrompt: "По истечении (секунд):",
            MinutesPrompt: "По истечении (минут):",
            Whichlock: "Замок:",
            WhichContactSensor: "Контактный датчик:",
            LockWhenClosed: "Закрыть замок по сигналу 'закрыто' с датчика?",
            UnlockWhenOpened: "Открыть замок по сигналу 'открыто' с датчика?",
            ReLockIfClosed: "Закрыть замок повторно, если закрыто и замок открыт?",
            LowBatteryNotifications: "Отправлять уведомления о низком заряде батареи?",
            BatteryWarningLevel: "Предупредить, если ниже, чем:",
            BatteryCriticalLevel: "Критическое, если ниже чем:",
            RepeatLowBatteryNotifications: "Повторять уведомления о низком заряде батареи?",
            OpenCloseNotifications: "Отправлять уведомления при открытии/закрытии?",
            LockUnlockNotifications: "Отправлять уведомления при открытии/закрытии замка?",
            NotifyLeftOpen: "Уведомление если оставлено открытым?",
            LabelPrompt: "Имя приложения:",
            Language: "Язык (Language):"]

        default: return getPreferencesResources(getDefaultLanguage())
    }
}

private def getMessagesResources(language = null)
{
    switch (getLanguageOrSelected(language))
    {
        case "EN": return [
            Closed: "%s: closed.",
            Opened: "%s: opened.",
            locked: "locked",
            unlocked: "unlocked",
            Locked: "%s: locked.",
            Unlocked: "%s: unlocked.",
            LockedButOpened: "%s is locked but %s is open.",
            Relocked: "%s has been re-locked after %s seconds timeout.",
            LockStateIsInvalid: "%s is in invalid or unknown state: %s.",
            LockStateRecovered: "%s has recovered and now in state: %s.",
            OperationFailed: "Warning! %s: failed to do '%s'.",
            LowBattery: "Warning! %s has low battery (%s%%).",
            CriticalBattery: "Warning! %s battery level is critical (%s%%).",
            BatteryRecovered: "%s battery level back to normal (%s%%)."]

        case "RU": return [
            Closed: "%s: закрыто.",
            Opened: "%s: открыто.",
            locked: "закрыт",
            unlocked: "открыт",
            Locked: "%s: замок закрыт.",
            Unlocked: "%s: замок открыт.",
            LockedButOpened: "%s закрыт, но %s - 'открыт'.",
            Relocked: "%s был повторно закрыт после %s секунд.",
            LockStateIsInvalid: "%s находится в некорректном или неизвестном состоянии: %s.",
            LockStateRecovered: "Состояние %s восстановлено: %s.",
            OperationFailed: "Внимание! %s: ошибка при выполнение операции '%s'.",
            LowBattery: "Внимание! %s имеет низкий заряд батареи: (%s%%).",
            CriticalBattery: "Внимание! %s имеет критический заряд батареи: (%s%%).",
            BatteryRecovered: "Заряд батареи %s восстановлен: (%s%%)."]

        default: return getMessagesResources(getDefaultLanguage())
    }
}

private def getDefaultLanguage()
{
    return getSupportedLanguages().keySet().toArray()[0]
}

private def getLanguageOrSelected(language)
{
    return language ?: getSelectedLanguage()
}

private def getSelectedLanguage()
{
    return getValueOrDefault("selectedLanguage", getDefaultLanguage())
}

// preferences
preferences
{
    page(name: "preferencesPage")
}

private def preferencesPage()
{
    def resources = getPreferencesResources()
    
    dynamicPage(name: "preferencesPage", title: resources.Preferences, install: true, uninstall: true)
    {
        def secondsValuePrompt = resources.SecondsPrompt

        section()
        {
            paragraph "${getApplicationInfo(resources)}"
        }

        section(resources.Interface)
        {
            input "selectedLanguage", "enum", title: resources.Language, options: getSupportedLanguages(), defaultValue: getSelectedLanguage(), submitOnChange: true, required: true
        }

        section(resources.Sources)
        {
            input "lock0", "capability.lock", title: resources.Whichlock, required: true
            input "contact0", "capability.contactSensor", title: resources.WhichContactSensor, required: true
        }

        section(resources.Actions)
        {
            input "shouldAutoLock", "bool", title: resources.LockWhenClosed, defaultValue: getValueOrDefault("shouldAutoLock", true), submitOnChange: true, required: false
            
            if (shouldAutoLock)
                input "autoLockDelay", "number", title: secondsValuePrompt, defaultValue: getValueOrDefault("autoLockDelay", 2), required: false
            
            input "shouldAutoUnlock", "bool", title: resources.UnlockWhenOpened, defaultValue: getValueOrDefault("shouldAutoUnlock", false), submitOnChange: true, required: false
            
            if (shouldAutoUnlock)
                input "autoUnlockDelay", "number", title: secondsValuePrompt, defaultValue: getValueOrDefault("autoUnlockDelay", 0), required: true
            
            input "shouldAutoReLock", "bool", title: resources.ReLockIfClosed, defaultValue: getValueOrDefault("shouldAutoReLock", false), submitOnChange: true, required: false
            
            if (shouldAutoReLock)
                input "autoReLockDelay", "number", title: secondsValuePrompt, defaultValue: getValueOrDefault("autoReLockDelay", 120), required: true
        }

        section(resources.Notifications)
        {
            input "shouldSendOpenCloseNotifications", "bool", title: resources.OpenCloseNotifications, defaultValue: getValueOrDefault("shouldSendOpenCloseNotifications", false), submitOnChange: true, required: false
            input "shouldSendLockUnlockNotifications", "bool", title: resources.LockUnlockNotifications, defaultValue: getValueOrDefault("shouldSendLockUnlockNotifications", false), submitOnChange: true, required: false
            
            input "shouldSendLeftOpenNotification", "bool", title: resources.NotifyLeftOpen, defaultValue: getValueOrDefault("shouldSendLeftOpenNotification", false), submitOnChange: true, required: false

            if (shouldSendLeftOpenNotification)
                input "leftOpenNotificationDelay", "number", title: secondsValuePrompt, defaultValue: getValueOrDefault("leftOpenNotificationDelay", 180), required: true
                
            input "shouldSendLowBatteryNotifications", "bool", title: resources.LowBatteryNotifications, defaultValue: getValueOrDefault("shouldSendLowBatteryNotifications", false), submitOnChange: true, required: false
            
            if (shouldSendLowBatteryNotifications)
            {
                input "lowBatteryWarningThreshold", "number", title: resources.BatteryWarningLevel, range: "1..100", defaultValue: getValueOrDefault("lowBatteryWarningThreshold", 35), required: true
                input "lowBatteryCriticalThreshold", "number", title: resources.BatteryCriticalLevel, range: "1..50", defaultValue: getValueOrDefault("lowBatteryCriticalThreshold", 15), required: true
                
                input "shouldRepeatLowBatteryNotifications", "bool", title: resources.RepeatLowBatteryNotifications, defaultValue: getValueOrDefault("shouldRepeatLowBatteryNotifications", true), submitOnChange: true, required: false
                
                if (shouldRepeatLowBatteryNotifications)
                    input "lowBatteryRepeatInterval", "number", title: resources.MinutesPrompt, range: "5..*", defaultValue: getValueOrDefault("lowBatteryRepeatInterval", 30), required: true
            }
        }
        
        section(resources.Other)
        {
            label title: resources.LabelPrompt, required: false
        }
    }
}

private def getValueOrDefault(String settingName, defaultValue)
{
    return settings.containsKey(settingName)
        ? settings[settingName]
        : (settings[settingName] = defaultValue)
}

private def getApplicationInfo(resources = null)
{
    return "${app.getName()}. ${resources?.Version ?: 'Version'}: ${getVersion()} (${getModificationDate().getDateString()})"
}

// constants
private def initializeConstants()
{
    // in seconds
    state.delays = [
        maxLockUnlockAttempts: toIntegerOrDefault(appSettings.maxLockUnlockAttempts, 2),
        lockUnlockAttemptDelay: toIntegerOrDefault(appSettings.lockUnlockAttemptDelay, 15),
        lockActualStateCheckDelay: toIntegerOrDefault(appSettings.lockActualStateCheckDelay, 10)]

    state.constants = [
        openedStateValue: "open",
        closedStateValue: "closed",
        lockedStateValue: "locked",
        unlockedStateValue: "unlocked"]
}

private def toIntegerOrDefault(value, Integer defaultValue = 0)
{
	return value.isInteger() ? value as Integer : defaultValue
}

// initialization
def installed()
{
    log.debug "Installing..."

    doInitialize()
}

def updated()
{
    log.debug "Updating..."

    doInitialize()
    forceUpdateState()
}

private def doInitialize()
{
    log.debug "Initializing..."
    log.info getApplicationInfo()
    
    unsubscribe()
    unschedule()
    initializeConstants()
    
    log.debug "Subscribing to $lock0 events..."
    subscribe(lock0, "lock", lockHandler)
    subscribe(lock0, "unlock", lockHandler)

    log.debug "Subscribing to $contact0 events..."
    subscribe(contact0, "contact.open", contactHandler)
    subscribe(contact0, "contact.closed", contactHandler)
    
    if (shouldSendLowBatteryNotifications)
    {
        log.debug "Starting batteries level monitoring..."
        subscribe(lock0, "battery", batteryHandler)
        subscribe(contact0, "battery", batteryHandler)
    }
    
    log.debug "Initialization done."
}

private def forceUpdateState()
{
    log.debug "Reporting current devices states after update..."

    checkBatteries()
    onContactStateChanged(getContactState())
}

// event handlers
def batteryHandler(evt)
{
    onBatteryStateChanged(evt.device, evt.integerValue)
}

def checkBatteries()
{
    onBatteryStateChanged(lock0)
    onBatteryStateChanged(contact0)
}

private def onBatteryStateChanged(device)
{
    onBatteryStateChanged(device, getBatteryLevel(device))
}

private def onBatteryStateChanged(device, int batteryLevel)
{
    log.trace "Handling $device battery level: ${batteryLevel}%."

    def isLowBattery = true
    
    unsubscribe(checkBatteries)
    
    if (batteryLevel <= lowBatteryCriticalThreshold)
        sendBatteryNotification("CriticalBattery", device, batteryLevel)
    else if (batteryLevel <= lowBatteryWarningThreshold)
        sendBatteryNotification("LowBattery", device, batteryLevel)
    else 
    {   
        isLowBattery = false
    
        if (state.isLowBattery)
            sendBatteryNotification("BatteryRecovered", device, batteryLevel)
    }

    if (isLowBattery && shouldRepeatLowBatteryNotifications)
        runIn(lowBatteryRepeatInterval * 60, checkBatteries)

    state.isLowBattery = isLowBattery
}

def lockHandler(evt)
{
    onLockStateChanged(evt.value)
}

private def onLockStateChanged(lockState)
{
    log.trace "Handling $lock0 state: $lockState."

    if (lockState == state.constants.lockedStateValue)
    {
        if (isOpened())
            sendLockUnlockNotification("LockedButOpened", contact0)
        else if (state.isReLocking)
            sendLockUnlockNotification("Relocked", autoReLockDelay.toString())
        else
            sendLockUnlockNotification("Locked")
            
        unscheduleHandlersAndReset()
    }
    else if (lockState == state.constants.unlockedStateValue)
    {
        sendLockUnlockNotification("Unlocked")
        unscheduleHandlersAndReset()
        
        if (shouldAutoReLock)
        {
            log.debug "Will try to re-lock in $autoReLockDelay seconds if $contact0 is closed."
            runIn(autoReLockDelay, relockSelectedLock)
        }
    }
    else
        log.debug "Cannot handle $lock0 state: $lockState"
}

def contactHandler(evt)
{
    onContactStateChanged(evt.value)
}

private def onContactStateChanged(contactState)
{
    log.trace "Handling $contact0 state: $contactState."

    if (contactState == state.constants.closedStateValue)
    {
        sendOpenCloseNotification("Closed")

        unscheduleHandlersAndReset()
        unschedule(sendLeftOpenNotification)

        if (shouldAutoLock && isUnlocked())
        {
            log.debug "$contact0 is closed - will try to lock after $autoLockDelay seconds."
            runIn(autoLockDelay, lockSelectedLock)
            return
        }
    }
    else if (contactState == state.constants.openedStateValue)
    {
        sendOpenCloseNotification("Opened")

        unscheduleHandlersAndReset()
        unschedule(sendLeftOpenNotification)

        if (sendLeftOpenNotification)
        {
            log.debug "Will notify if $contact0 left open after $autoUnlockDelay seconds."
            runIn(leftOpenNotificationDelay, sendLeftOpenNotification)
        }
        
        if (shouldAutoUnlock && isLocked())
        {
            log.debug "$contact0 is open - will try to unlock after $autoUnlockDelay seconds."
            runIn(autoUnlockDelay, unlockSelectedLock)
            return
        }
    }
    else
        log.debug "Cannot handle $contact0 state: $contactState"
        
    ensureLockStateIsValid()
}

// actions
def lockSelectedLock()
{
    if (isClosed())
    {
        if (!isLocked())
        {
            log.debug "Sending lock command to $lock0."
            lock0.lock()
        }

        unschedule(ensureLockStateIsValid)
        runIn(state.delays.lockActualStateCheckDelay, ensureLockStateIsValid)
    }
    else
    {
        log.debug "$contact0 is still open, trying to lock $lock0 again in $autoLockDelay seconds"

        unschedule(lockSelectedLock)
        runIn(autoLockDelay, lockSelectedLock)
    }
}

def unlockSelectedLock()
{
    if (isOpened())
    {
        if (isLocked())
        {
            log.debug "Sending unlock command to $lock0."
            lock0.unlock()
        }

        unschedule(ensureLockStateIsValid)
        runIn(state.delays.lockActualStateCheckDelay, ensureLockStateIsValid)
    }
}

def relockSelectedLock()
{
    state.isReLocking = true
    lockSelectedLock()
}

def ensureLockStateIsValid()
{
    log.debug "Ensure $lock0 state is valid..."

    if (isLocked() && isOpened())
        tryAdjustActualLockState("unlock", unlockSelectedLock)
    else if (isUnlocked() && isClosed())
        tryAdjustActualLockState("lock", lockSelectedLock)
    else
    {
        unscheduleHandlersAndReset()

        if (!isLocked() && !isUnlocked())
        {
            log.debug "$contact0 state: ${getContactState()}."
            sendLockUnlockNotification("LockStateIsInvalid", getMessage(getLockState()))
        }
        else
        {
            unscheduleHandlersAndReset()

            if (state.isLockUnlockFailed)
            {
                state.isLockUnlockFailed = false
                sendLockUnlockNotification("LockStateRecovered", getMessage(getLockState()))
            }
            else
                log.debug "$lock0 state is valid: $lockState."
        }
    }
}

def tryAdjustActualLockState(String type, handler)
{
    def maxLockUnlockAttempts = state.delays.maxLockUnlockAttempts
    def lockUnlockAttempts = (state.lockUnlockAttempts = state.lockUnlockAttempts + 1)
    
    if (lockUnlockAttempts < maxLockUnlockAttempts)
    {
        log.trace "$lock0 $type attempt #$lockUnlockAttempts of $maxLockUnlockAttempts."

        unschedule(handler)
        runIn(state.delays.lockUnlockAttemptDelay, handler)
    }
    else
    {
        state.isLockUnlockFailed = true
        sendLockUnlockNotification("OperationFailed", type)
        unschedule(handler)
        unschedule(ensureLockStateIsValid)
    }
}

private def unscheduleHandlersAndReset()
{
    unschedule(lockSelectedLock)
    unschedule(unlockSelectedLock)
    unschedule(relockSelectedLock)
    unschedule(ensureLockStateIsValid)

    state.isReLocking = false
    state.lockUnlockAttempts = 0
}

// states
private def isLocked()
{
    return getLockState() == state.constants.lockedStateValue
}

private def isUnlocked()
{
    return getLockState() == state.constants.unlockedStateValue
}

private def isClosed()
{
    return getContactState() == state.constants.closedStateValue
}

private def isOpened()
{
    return getContactState() == state.constants.openedStateValue
}

private def getLockState()
{
    return lock0.latestValue("lock")
}

private def getContactState()
{
    return contact0.latestValue("contact")
}

private def getBatteryLevel(device)
{
    return device.currentValue("battery").toInteger()
}

// notifications
def sendLockUnlockNotification(String messageKey, Object... args)
{
    sendNotification(getMessage(messageKey), shouldSendLockUnlockNotifications, lock0, args)
}

def sendOpenCloseNotification(String messageKey, Object... args)
{
    sendNotification(getMessage(messageKey), shouldSendOpenCloseNotifications, contact0, args)
}

def sendBatteryNotification(String messageKey, Object... args)
{
    sendNotification(getMessage(messageKey), shouldSendLowBatteryNotifications, args);
}

def sendLeftOpenNotification()
{
    sendNotification(getMessage("LeftOpenNotification"), shouldSendLeftOpenNotification)
}

private def getMessage(String messageKey)
{
    return getMessagesResources()[messageKey] ?: "<undefined message>"
}

private def sendNotification(String message, boolean shouldSendPushMessage, Object... args)
{
	def msg = String.format(message, [args].flatten() as Object[])

    log.info "Notification (with push message: $shouldSendPushMessage): $msg"

    if (shouldSendPushMessage)
    	sendPush msg
}