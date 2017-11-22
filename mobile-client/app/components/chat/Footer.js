import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {StyleSheet, TouchableOpacity, View} from "react-native";
import Icon from 'react-native-vector-icons/FontAwesome';

export default class Footer extends Component {

    constructor(props) {
        super(props);
        this.state = {
            position: this.props.position
        }
    }

    isSelected(position) {
        return this.state.position === position
    }

    onPress(position) {
        this.setState({position: position});
        this.props.onButtonPress(position);
    }

    render() {

        const getIconColor = (position) => {
            const activeIconColor = '#3c8dbc';
            const passiveIconColor = '#666666';
            return this.isSelected(position) ? activeIconColor : passiveIconColor
        };

        return (
            <View style={styles.chatFooter}>
                {
                    this.props.buttonNames.map((name, index) => {
                        return (
                            <TouchableOpacity
                                key={index}
                                style={styles.chatFooterBtn}
                                onPress={() => this.onPress(index)}
                                disabled={this.isSelected(index)}
                                activeOpacity={0.8}>
                                <Icon
                                    name={name}
                                    style={styles.chatFooterIcon}
                                    color={getIconColor(index)}/>
                            </TouchableOpacity>
                        )
                    })
                }
            </View>
        )
    }
}

Footer.defaultProps = {
    buttonNames: [],
    onButtonPress: (position) => console.log("Pressed button", position)
};

Footer.propTypes = {
    buttonNames: PropTypes.arrayOf(PropTypes.string), // FontAwesome icon names
    onButtonPress: PropTypes.func,
    position: PropTypes.number
};

const styles = StyleSheet.create({
    chatFooter: {
        flexDirection: "row",
        height: 60,
        borderTopWidth: 1,
        borderColor: '#cccccc',
        backgroundColor: '#eeeeee',
    },
    chatFooterBtn: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center'
    },
    chatFooterIcon: {
        fontSize: 36
    }
});