<template>
  <div class="message-container">
    <div v-for="(message, ind) in messageList" class="message" :class="message.type" :id="'message-' + ind" :key="message.id" @click="removeMessage(ind)">
      {{ message.msg }}
    </div>
  </div>
</template>

<script>
export default {
  mounted () {
  },
  computed: {
    messageList () {
      return this.$store.getters.getMessageList
    },
    permanentImages () {
      return this.$store.getters.getPermanentImages
    }
  },
  methods: {
    removeMessage (message) {
      this.$store.dispatch('removeMessage', message)
    }
  }
}
</script>

<style scoped lang="scss">
  .message-container {
    position: fixed;
    right: 5px;
    top: 10px;
    width: 181px;
    z-index: 1001;
    display: flex;
    flex-direction: column;
    color: #ffffff;
  }

  .message {
    display: flex;
    width: 99%;
    margin-top: 8px;
    min-height: 50px;
    border-radius: 2px;
    justify-content: center;
    align-items: center;
    font-size: 14px;
    line-height: 16px;
    cursor: pointer;
    padding: 20px;
  }

  .info {
    border: 1px solid rgb(0, 0, 255);
    background-color: rgba(0, 0, 255, 0.2);
  }

  .success {
    background-color: rgba(184,233,134,0.2);
    border: 1px solid rgb(184, 233, 134);
  }

  .warn {
    border: 1px solid rgb(255, 0, 0);
    background-color: rgba(255, 0, 0, 0.2);
  }

  .message:nth-child(n+2) {
    opacity: 0.5;
  }

  .close-message {
    width: 20px;
    height: 20px;
    position: relative;
    border-radius: 2px;
    float: left;
  }

  .close-message::after, .close-message::before{
    position: absolute;
    top: 9px;
    left: 0px;
    content: '';
    display: block;
    width: 20px;
    height: 2px;
    background-color: #FFFFFF;
  }

  .close-message::after {
    transform: rotate(45deg);
  }

  .close-message::before {
    transform: rotate(-45deg);
  }

  /* Large Phones, landscape*/
  @media only screen and (max-width : 992px) {
    .message-container {
      width: 200px;
      right: 10px;
    }

    .message {
      height: 20px;
      font-size: 10px;
    }
  }

  /* Small Devices */
  @media only screen and (max-width : 667px) {
    .message-container {
      display: none;
    }
  }

  /* Extra Small Devices, Phones */
  @media only screen and (max-width : 480px) {

  }

  /* Custom, iPhone 5 Retina */
  @media only screen and (max-width : 320px) {

  }

</style>
