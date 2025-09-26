<template>
    <v-container>
            <v-row>
                <v-col>
                    <v-card>

                    
                    <v-card-title class="text-center text-h5">
                        회원목록
                    </v-card-title>

                    

                
                <v-card_text>

               
                <v-table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>이름</th>
                            <th>email</th>  
                            <th>채팅</th>
                        </tr>

                    </thead>
                        <tbody>
                            <tr v-for="member in memberList" :key="member.id">
                                <td>{{member.id}}</td>
                                <td>{{member.name}}</td>  
                                <td>{{member.email}}</td>

                              <!-- 프론트에서도 자기자신이랑 대화 못하게 막는 코드 추가-->
                                <td><v-btn color="primary"  :disabled ="member.email ===this.senderEmail"   @click="startChat(member.id)"> 채팅하기
                                </v-btn></td>

                            </tr>

                        </tbody>
                    
                </v-table>
            </v-card_text>

             
        </v-card>

    </v-col>
</v-row>


    </v-container>



</template>

<script>
import axios from "axios";
export default{
data(){
    return {
        memberList:[],
    //    자기자신이랑 대화 못하게 막는 코드 추가(me 임의로 추가한 코드 )
        senderEmail:null,

    }
}
    ,
   async created(){
     //    자기자신이랑 대화 못하게 막는 코드 추가(me 임의로 추가한 코드 )
        this.senderEmail=localStorage.getItem("email");


        const response= await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member/list`)
        this.memberList = response.data;

    },
    methods:{
        async startChat(otherMemberId){
            // 기존의 채팅방이 있으면 return 받고 없으면 새롭게 생성된 roomId return 
            const response=await axios.post(`${process.env.VUE_APP_API_BASE_URL}/chat/room/private/create?otherMemberId=${otherMemberId}`)
            const roomId=response.data;
            this.$router.push(`/chatpage/${roomId}`);
        }
    }
}


</script>