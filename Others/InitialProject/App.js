import React, { Component } from 'react';
import { Alert, Button, Text, View, Image, TouchableOpacity} from 'react-native';
// nvm use 14.1.0
// sudo npx react-native run-android
// sudo npx react-native start

var images = [
  require('./images/rock.png'),
  require('./images/scissors.png'),
  require('./images/paper.png')
];

var resultText = [
  "Remis!",
  "Gratulacje wygrałeś!",
  "Przeciwnik wygrał!"
];

class App extends Component {

  constructor(props) {
    super(props)
    this.state = {
      chosenImage : 2,
      isHiddenOptions : false,
      computerImage : 0,
      playerPoints : 0,
      computerPoints : 0,
      winner : 0
    }
  }

  // funnkcja wywołuje się, gdy wybierzemy jakiś symbol
  chooseImage(id) {
    var computerChoice = Math.floor(Math.random() * 3);
    this.setState({chosenImage: id});
    this.setState({computerImage: computerChoice});    
    this.setState({isHiddenOptions: true});
    
    if ((id == computerChoice) || 
      (id == computerChoice) || (id == computerChoice)) {
      this.setState({winner: 0});
    } else if ((id == 0 && computerChoice == 1) || 
      (id == 1 && computerChoice == 2) || (id == 2 && computerChoice == 0)) {
      this.setState({winner: 1});
      this.state.playerPoints += 1;
    } else {
      this.setState({winner: 2});
      this.state.computerPoints += 1;
    }
  }

  // definowanie interfejsu
  render() {
    return (
      <>
        <View style={{padding: 40, alignItems: "center"}}>
          <Text style={{fontSize: 20}}>PAPIER-KAMIEŃ-NOŻYCE</Text>
        </View>
        <View style={{flexDirection: "row", justifyContent: 'space-evenly'}}>
          <Text style={{fontSize: 20, fontStyle: "italic"}}>Twoje punkty:</Text>
          <Text style={{fontSize: 20, fontStyle: "italic"}}>Przeciwnika:</Text>
        </View>
        <View style={{padding: 5, flexDirection: "row", justifyContent: 'space-evenly'}}>
          <Text style={{fontSize: 30}}>{this.state.playerPoints}</Text>
          <Text style={{fontSize: 30}}>{this.state.computerPoints}</Text>
        </View>

        { this.state.isHiddenOptions ?
          <>
            <View style={{flexDirection: "row", justifyContent: 'space-evenly', padding: 50}}>
              <Image source={images[this.state.chosenImage]} style={{height: 120, width: 120, resizeMode : 'stretch'}} />
              <Image source={images[this.state.computerImage]} style={{height: 120, width: 120, resizeMode : 'stretch'}} />          
            </View>
            <View style={{flexDirection: "row", justifyContent: 'space-evenly', padding: 50}}>
              <Text style={{fontStyle: "italic"}}> {resultText[this.state.winner]} </Text>
            </View>
            <View>
              <Button title="Spróbuj jeszcze raz!" onPress={() => this.setState({isHiddenOptions: false})} />
            </View>
          </>
          : null
        }   
        { !this.state.isHiddenOptions ?              
          <View style={{flex: 1, justifyContent: "flex-end"}}>
            <View style={{padding:10, alignItems: "center"}}>
              <Text style={{fontSize: 23}}>Wybierz symbol</Text>              
            </View>
            <View style={{flexDirection: "row", marginBottom: 50}}>
              <TouchableOpacity onPress={() => this.chooseImage(0)}>
                <Image source={images[0]} style={{height: 120, width: 120, resizeMode : 'stretch'}} />
              </TouchableOpacity>
              <TouchableOpacity onPress={() => this.chooseImage(1)}>
                <Image source={images[1]} style={{height: 120, width: 120, resizeMode : 'stretch'}} />
              </TouchableOpacity>         
              <TouchableOpacity onPress={() => this.chooseImage(2)}>
                <Image source={images[2]} style={{height: 120, width: 120, resizeMode : 'stretch'}} />
              </TouchableOpacity>            
            </View>
          </View> 
          : null
        }  
      </>     
    );
  }
}

export default App;
