export type CreateRegistrationResponse = {
    lectureId: number;
    registrationId: number;
}

export type CreateRegistrationRequest = {
    lectureId: number;
}

export type RegistrationLecture = {
    lectureId: number;
    name: string;
    capacity: number;
    registrationId: number;
}