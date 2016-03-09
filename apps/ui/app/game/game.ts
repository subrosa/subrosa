import {Account} from "../account/account";
interface Image {
    id: number,
    link: URL,
}

interface Post {
    id: number,
    account: Account,
    postType: string,
    content: string,
    created: Date,
    modified: Date,
}

interface Event {
    id: number,
}

interface PlayerAttr {
    name: string,
    description: string,
    type: string,
    required: boolean,
    fieldId: string,
}

export interface Game {
    id: number,
    url: string,
    status: string,
    image: Image,
    name: string,
    description: string,
    gameType: string,
    registrationStart: Date,
    registrationEnd: Date,
    gameStart: Date,
    gameEnd: Date,
    price: number,
    playerInfo: PlayerAttr[],
    rules: any,
    posts: Post[],
    history: any,
    events: Event[],
    created: Date,
    modified: Date,
}
