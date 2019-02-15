import {NativeModules, DeviceEventEmitter} from 'react-native';

const NativeRNSumup = NativeModules.RNSumup;

class RNSumup {

    static login() {
        NativeRNSumup.login();
    }

    static charge(amount) {
        NativeRNSumup.charge(amount);
    }

    static paymentSettings() {
        NativeRNSumup.paymentSettings();
    }

    static logout() {
        NativeRNSumup.logout();
    }

    static async isLoggedIn() {
        const {loggedIn} = await NativeRNSumup.isLoggedIn();
        return loggedIn;
    }

    static prepareCardTerminal() {
        NativeRNSumup.prepareCardTerminal();
    }

    static addLoginListener(callback) {
        DeviceEventEmitter.addListener('loggedIn', callback);
    }

    static addPaymentSuccessListener(callback) {
        DeviceEventEmitter.addListener('paymentSuccess', callback);
    }

    static removePaymentSuccessListener(callback) {
        DeviceEventEmitter.removeListener('paymentSuccess', callback);
    }

    static addPaymentFailedListener(callback) {
        DeviceEventEmitter.addListener('paymentFailed', callback);
    }

    static removePaymentFailedListener(callback) {
        DeviceEventEmitter.removeListener('paymentFailed', callback);
    }

    static removeLoginListener(callback) {
        DeviceEventEmitter.removeListener('loggedIn', callback);
    }
}

export default RNSumup;
