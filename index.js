import {NativeModules} from 'react-native';

const NativeRNSumup = NativeModules.RNSumup;

class RNSumup {

    static login() {
        NativeRNSumup.login();
    }

    static charge(amount) {
        NativeRNSumup.charge(amount);
    }
}

export default RNSumup;
