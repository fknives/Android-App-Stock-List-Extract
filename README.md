# Stock-List-Extract-Android
Simple App to extract the list of ticker symbols from R

## IF you need only the file: last updated at 2021.02.14
file: [stock_list_1613311888669.csv](https://github.com/fknives/Stock-List-Extract-Android/blob/dev/stock_list_1613311888669.csv)

## Setup:
- create a config.gradle file
- give the needed resource ids. you may figure it out with the syncservice
- I don't want them to be in the repo, I don't know if it would cause any trouble

## Usage:
- install the application on your phone
- Start the application
- Click on Start Service
- Enable Accessibility Service (Downloaded Services)
- Click on Start Service again
- Notice a notification is shown (if not force stop the app and start from point 2)
- Navigate to R All Stocks List
- Tap on the Notification
- Notice the app starts to load from the screen (the notification is updated)
- Wait until the notification disappears and the screen is scrolled until the end
- Go back to this app
- Notice it now shows all the tickers at the bottom and you can send the file to somewhere else

## Notes:
- the app does not have internet permission, so you don't have to worry that something is leaving your device that you do not want to
- after finishing the sync the AccessibilityService is disabled (at least above api 24, if you have a solution below please let me know)
- the file format is CSV meaning the tickers are separated by comma, easy to include into google sheets or other programs.
