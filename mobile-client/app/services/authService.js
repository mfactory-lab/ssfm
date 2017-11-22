import { AsyncStorage } from "react-native";
import { CommunicationService } from "./communicationService";
import Auth0 from 'react-native-auth0';
import config from "../../app.config";

const auth0 = new Auth0({
    domain: config.auth0domain,
    clientId: config.auth0clientId
});


let instance = null;

// Singleton
export class AuthService {

    constructor() {
        this.communicationService = new CommunicationService();
        if (!instance) {
            instance = this;
        }
        return instance;
    }

    login() {
        return AsyncStorage.getItem('user')
            .then((user) => {
                const _user = user ? JSON.parse(user) : null;
                if (_user) {
                    return {
                        name: _user.name,
                        avatarUrl: _user.avatar
                    };
                }
            })
            .then((userInfo) => {
                return this.communicationService.login(userInfo)
            })
    }

    logout() {
        return AsyncStorage.removeItem('user')
          .then(() => this.communicationService.logout())
    }

    authenticate() {
        return auth0.webAuth.authorize({ scope: 'openid profile', audience: `https://${config.auth0domain}/userinfo` })
            .then(credentials => {
                return auth0.auth.userInfo({ token: credentials.accessToken })
            })
            .then((user) => {
                return {
                    name: user.name,
                    avatarUrl: user.picture
                }
            })
            .then((userInfo) => {
                return this.communicationService.login(userInfo)
            })
            .then((contactInfo) => {
                return AsyncStorage.setItem('user', JSON.stringify(contactInfo))
            })
    }

}