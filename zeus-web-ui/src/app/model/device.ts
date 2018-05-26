export class Device {
  id: string;
  name: string;
  state: string;
  version: number;

  // additional meta attributes
  $editing: boolean;
  $error: boolean;
}
