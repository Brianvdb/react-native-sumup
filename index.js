import {NativeModules} from 'react-native';

const NativeRNSumup = NativeModules.RNSumup;

class RNSumup {

    static login() {
        NativeRNSumup.login();
    }
}

export default RNSumup;
