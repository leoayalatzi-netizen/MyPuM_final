/**
 * MyPuM JavaScript Bridge
 * Communication layer between WebView and native Android
 */
const MyPuMBridge = {
    call(handler, method, params = {}) {
        if (window.AndroidBridge) {
            return window.AndroidBridge.invoke(handler, method, JSON.stringify(params));
        }
        console.warn('AndroidBridge not available');
        return null;
    }
};

window.MyPuMBridge = MyPuMBridge;
