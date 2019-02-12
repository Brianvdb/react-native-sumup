import {NativeModules} from 'react-native';

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
}

export default RNSumup;
