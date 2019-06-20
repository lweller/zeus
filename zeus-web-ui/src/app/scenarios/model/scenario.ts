export class Scenario {
    id: string;
    name: string;
    enabled: boolean;
    version: number;

    // additional meta attributes
    $editing: boolean;
    $error: boolean;
}
