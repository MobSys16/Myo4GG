package de.uni_luebeck.imis.gestures.model;

/**
 * @author Finn
 * @since 12.09.2016
 *
 * This Enum includes actions, that could be performed by the user, using the glass. These
 * actions are independent due to the gestures used to perform the action or the system that
 * detects the gestures (touch pad, myo). Actions are functional oriented, while gestures just
 * represent the input of the user.
 */
public enum Action {
    Photo,
    Pause,
    Stop,
    Cancel,
    Confirm,
    Previous,
    Back
}
