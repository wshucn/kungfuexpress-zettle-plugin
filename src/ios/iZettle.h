//
//  iZettle.h
//  iZettle
//
//  Created by MobileStar on 24/9/17.
//
//

#import <Cordova/CDVPlugin.h>


@interface iZettle : CDVPlugin
/*
 iZettle Support On/Off
 Detect iZettle connected
 Get Device Name
 Get iZettle Interface Version
 */

- (void)initialize:(CDVInvokedUrlCommand*)command;
- (void)charge:(CDVInvokedUrlCommand*)command;
- (void)refund:(CDVInvokedUrlCommand*)command;
- (void)retrievePaymentInfo:(CDVInvokedUrlCommand*)command;
- (void)presentSettings:(CDVInvokedUrlCommand*)command;
- (void)enforceAccount:(CDVInvokedUrlCommand*)command;
- (void)logout:(CDVInvokedUrlCommand*)command;

@end
