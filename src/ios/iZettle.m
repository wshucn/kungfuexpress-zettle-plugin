//
//  iZettle.m
//  iZettle
//
//  Created by Mobile Star on 25/9/17.
//
//

#import <Cordova/CDVAvailability.h>
#import "iZettle.h"

@import iZettleSDK;
#import <iZettleSDK/iZettleSDK.h>

#define iZettleHasInitializedKey @"iZettleHasInitializedKey"

@interface iZettle()

@end

@implementation iZettle {}

- (void)dealloc
{

}

/*
 *  pluginInitialize
 */
- (void)pluginInitialize
{
}

- (void)onAppTerminate
{
    [[NSUserDefaults standardUserDefaults] setBool:NO forKey:iZettleHasInitializedKey];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (void)initialize:(CDVInvokedUrlCommand*)command
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    BOOL hasInitialized = [userDefaults boolForKey:iZettleHasInitializedKey];
    if (hasInitialized) {
        return;
    }

    NSString* clientId = command.arguments[0];
    NSString* callbackURL = command.arguments[1];
    NSError* error;
    iZettleSDKAuthorization* authenticationProvider = [[iZettleSDKAuthorization alloc] initWithClientID:clientId callbackURL:[NSURL URLWithString:callbackURL] error:&error enforcedUserAccount:^NSString * _Nullable{
        return nil;
    }];
    [[iZettleSDK shared] startWithAuthorizationProvider:authenticationProvider];

    CDVPluginResult* pluginResult;
    if (error) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [userDefaults setBool:YES forKey:iZettleHasInitializedKey];
        [userDefaults synchronize];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)charge:(CDVInvokedUrlCommand*)command
{
    double amount = [command.arguments[0] doubleValue];
    double roundedAmount = round(amount * 100) / 100;
    NSDecimalNumber* decimalAmount = [[NSDecimalNumber alloc] initWithDouble:roundedAmount];

    NSString* reference = command.arguments[1];
    if ([reference isKindOfClass:[NSNull class]]) {
        reference = nil;
    }

    [[iZettleSDK shared] chargeAmount:decimalAmount enableTipping:NO reference:reference presentFromViewController:self.viewController completion:^(iZettleSDKPaymentInfo * _Nullable paymentInfo, NSError * _Nullable error) {

        CDVPluginResult* pluginResult;
        if(paymentInfo != nil) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:paymentInfo.dictionary];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)refund:(CDVInvokedUrlCommand*)command
{
    double amount = [command.arguments[0] doubleValue];
    NSString* reference = command.arguments[1];
    NSString* refundReference = command.arguments[2];

    NSDecimalNumber* decimalAmount = [[NSDecimalNumber alloc] initWithDouble:amount];

    [[iZettleSDK shared] refundAmount:decimalAmount ofPaymentWithReference:reference refundReference:refundReference presentFromViewController:self.viewController completion:^(iZettleSDKPaymentInfo * _Nullable paymentInfo, NSError * _Nullable error) {

        CDVPluginResult* pluginResult;
        if(paymentInfo != nil) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:paymentInfo.dictionary];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)retrievePaymentInfo:(CDVInvokedUrlCommand*)command
{
    NSString* reference = command.arguments[0];
    [[iZettleSDK shared] retrievePaymentInfoForReference:reference presentFromViewController:self.viewController completion:^(iZettleSDKPaymentInfo *paymentInfo, NSError *error) {

        CDVPluginResult* pluginResult;
        if(paymentInfo != nil) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:paymentInfo.dictionary];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
        }
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

- (void)presentSettings:(CDVInvokedUrlCommand*)command
{
    [[iZettleSDK shared] presentSettingsFromViewController:self.viewController];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)enforceAccount:(CDVInvokedUrlCommand*)command
{
    NSString* email = command.arguments[0];
    NSString* clientId = command.arguments[1];
    NSString* callbackURL = command.arguments[2];
    NSError* error;
    iZettleSDKAuthorization* authenticationProvider = [[iZettleSDKAuthorization alloc] initWithClientID:clientId callbackURL:[NSURL URLWithString:callbackURL] error:&error enforcedUserAccount:^NSString * _Nullable{
        return email;
    }];
    [[iZettleSDK shared] startWithAuthorizationProvider:authenticationProvider];

    CDVPluginResult* pluginResult;
    if (error) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)logout:(CDVInvokedUrlCommand*)command
{
    [[iZettleSDK shared] logout];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}
    
@end
