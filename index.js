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

    static async isLoggedIn() {
        const {loggedIn} = await NativeRNSumup.isLoggedIn();
        return loggedIn;
    }

    static addLoginListener(callback) {
        DeviceEventEmitter.addListener('loggedIn', callback);
    }

    static removeLoginListener(callback) {
        DeviceEventEmitter.removeListener('loggedIn', callback);
    }
}

export default RNSumup;
