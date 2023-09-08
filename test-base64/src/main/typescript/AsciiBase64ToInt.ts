class AsciiBase64ToInt {
    private base64Chars: string;
    private charIndexArray: number[];

    constructor() {
        this.base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        this.charIndexArray = new Array(128).fill(-1);
        for (let i = 0; i < this.base64Chars.length; i++) {
            const char = this.base64Chars.charAt(i);
            this.charIndexArray[char.charCodeAt(0)] = i;
        }
    }

    decodeCustomBase64(customBase64: string): number {
        let result = 0;
        for (let i = 0; i < customBase64.length; i++) {
            const char = customBase64.charAt(i);
            const charIndex = this.charIndexArray[char.charCodeAt(0)];
            result = result * 64 + charIndex;
        }
        return result;
    }
}

// Exemple d'utilisation
const customBase64 = "BAC"; // Remplacez cette chaîne par celle que vous souhaitez décoder
const decoder = new CustomBase64Decoder();
const decodedNumber = decoder.decodeCustomBase64(customBase64);

if (decodedNumber !== null) {
    console.log("Nombre décodé : " + decodedNumber);
} else {
    console.log("La chaîne personnalisée n'est pas valide.");
}
