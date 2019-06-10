export class Event {
  id: string;
  name: string;
  nextScheduledExecution: string;
  version: number;

  // additional meta attributes
  $error: boolean;
}
