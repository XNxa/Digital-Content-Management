export enum Status {
    PLANIFIE = 'PLANIFIE',
    PUBLIE = 'PUBLIE',
    EN_ATTENTE = 'EN_ATTENTE',
    NON_PUBLIE = 'NON_PUBLIE',
    ARCHIVE = 'ARCHIVE'
}

export namespace Status {
    export function printableString(status: Status): string {
        switch (status) {
            case Status.PLANIFIE:
                return 'Planifié';
            case Status.PUBLIE:
                return 'Publié';
            case Status.EN_ATTENTE:
                return 'En attente';
            case Status.NON_PUBLIE:
                return 'Non publié';
            case Status.ARCHIVE:
                return 'Archivé';
        }
    }

    export function getStringList() : string[] {
        return [printableString(Status.PLANIFIE), printableString(Status.PUBLIE), printableString(Status.EN_ATTENTE), printableString(Status.NON_PUBLIE), printableString(Status.ARCHIVE)]
    }

    export function fromString(status: string): Status {
        switch (status) {
            case 'Planifié': case 'PLANIFIE':
                return Status.PLANIFIE;
            case 'Publié': case 'PUBLIE':
                return Status.PUBLIE;
            case 'En attente': case 'EN_ATTENTE':
                return Status.EN_ATTENTE;
            case 'Non publié': case 'NON_PUBLIE':
                return Status.NON_PUBLIE;
            case 'Archivé': case 'ARCHIVE':
                return Status.ARCHIVE;
            default:
                throw new Error('Invalid status: ' + status);
        }
    }
}



