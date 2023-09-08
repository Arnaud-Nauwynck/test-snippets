
class CustomBase64Decoder {
    private base64Chars: string;

    constructor() {
        // Définir l'alphabet personnalisé de la Base64
        this.base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    }

    decodeCustomBase64(customBase64: string): number | null {
        let result = 0;

        for (let i = 0; i < customBase64.length; i++) {
            const char = customBase64.charAt(i);
            const charIndex = this.base64Chars.indexOf(char);

            if (charIndex === -1) {
                // Caractère non valide dans la Base64 personnalisée
                return null;
            }

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

